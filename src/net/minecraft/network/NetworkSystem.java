package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.network.NetHandlerHandshakeMemory;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LazyLoadBase<NioEventLoopGroup> SERVER_NIO_EVENTLOOP = new LazyLoadBase<>(() -> {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
   });
   public static final LazyLoadBase<EpollEventLoopGroup> SERVER_EPOLL_EVENTLOOP = new LazyLoadBase<>(() -> {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
   });
   private final MinecraftServer server;
   public volatile boolean isAlive;
   private final List<ChannelFuture> endpoints = Collections.synchronizedList(Lists.newArrayList());
   private final List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());

   public NetworkSystem(MinecraftServer p_i45292_1_) {
      this.server = p_i45292_1_;
      this.isAlive = true;
   }

   public void addEndpoint(@Nullable InetAddress p_151265_1_, int p_151265_2_) throws IOException {
      synchronized(this.endpoints) {
         Class<? extends ServerSocketChannel> oclass;
         LazyLoadBase<? extends EventLoopGroup> lazyloadbase;
         if (Epoll.isAvailable() && this.server.shouldUseNativeTransport()) {
            oclass = EpollServerSocketChannel.class;
            lazyloadbase = SERVER_EPOLL_EVENTLOOP;
            LOGGER.info("Using epoll channel type");
         } else {
            oclass = NioServerSocketChannel.class;
            lazyloadbase = SERVER_NIO_EVENTLOOP;
            LOGGER.info("Using default channel type");
         }

         this.endpoints.add((new ServerBootstrap()).channel(oclass).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel p_initChannel_1_) throws Exception {
               try {
                  p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
               } catch (ChannelException var3) {
               }

               p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("legacy_query", new LegacyPingHandler(NetworkSystem.this)).addLast("splitter", new NettyVarint21FrameDecoder()).addLast("decoder", new NettyPacketDecoder(EnumPacketDirection.SERVERBOUND)).addLast("prepender", new NettyVarint21FrameEncoder()).addLast("encoder", new NettyPacketEncoder(EnumPacketDirection.CLIENTBOUND));
               NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.SERVERBOUND);
               NetworkSystem.this.networkManagers.add(networkmanager);
               p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
               networkmanager.setNetHandler(new NetHandlerHandshakeTCP(NetworkSystem.this.server, networkmanager));
            }
         }).group(lazyloadbase.getValue()).localAddress(p_151265_1_, p_151265_2_).bind().syncUninterruptibly());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public SocketAddress addLocalEndpoint() {
      ChannelFuture channelfuture;
      synchronized(this.endpoints) {
         channelfuture = (new ServerBootstrap()).channel(LocalServerChannel.class).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel p_initChannel_1_) throws Exception {
               NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.SERVERBOUND);
               networkmanager.setNetHandler(new NetHandlerHandshakeMemory(NetworkSystem.this.server, networkmanager));
               NetworkSystem.this.networkManagers.add(networkmanager);
               p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
            }
         }).group(SERVER_NIO_EVENTLOOP.getValue()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
         this.endpoints.add(channelfuture);
      }

      return channelfuture.channel().localAddress();
   }

   public void terminateEndpoints() {
      this.isAlive = false;

      for(ChannelFuture channelfuture : this.endpoints) {
         try {
            channelfuture.channel().close().sync();
         } catch (InterruptedException var4) {
            LOGGER.error("Interrupted whilst closing channel");
         }
      }

   }

   public void tick() {
      synchronized(this.networkManagers) {
         Iterator<NetworkManager> iterator = this.networkManagers.iterator();

         while(iterator.hasNext()) {
            NetworkManager networkmanager = iterator.next();
            if (!networkmanager.hasNoChannel()) {
               if (networkmanager.isChannelOpen()) {
                  try {
                     networkmanager.tick();
                  } catch (Exception exception) {
                     if (networkmanager.isLocalChannel()) {
                        CrashReport crashreport = CrashReport.makeCrashReport(exception, "Ticking memory connection");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Ticking connection");
                        crashreportcategory.addDetail("Connection", networkmanager::toString);
                        throw new ReportedException(crashreport);
                     }

                     LOGGER.warn("Failed to handle packet for {}", networkmanager.getRemoteAddress(), exception);
                     ITextComponent itextcomponent = new TextComponentString("Internal server error");
                     networkmanager.sendPacket(new SPacketDisconnect(itextcomponent), (p_210474_2_) -> {
                        networkmanager.closeChannel(itextcomponent);
                     });
                     networkmanager.disableAutoRead();
                  }
               } else {
                  iterator.remove();
                  networkmanager.handleDisconnection();
               }
            }
         }

      }
   }

   public MinecraftServer getServer() {
      return this.server;
   }
}
