package net.minecraft.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CryptManager {
   private static final Logger LOGGER = LogManager.getLogger();

   @OnlyIn(Dist.CLIENT)
   public static SecretKey createNewSharedKey() {
      try {
         KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
         keygenerator.init(128);
         return keygenerator.generateKey();
      } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
         throw new Error(nosuchalgorithmexception);
      }
   }

   public static KeyPair generateKeyPair() {
      try {
         KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");
         keypairgenerator.initialize(1024);
         return keypairgenerator.generateKeyPair();
      } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
         nosuchalgorithmexception.printStackTrace();
         LOGGER.error("Key pair generation failed!");
         return null;
      }
   }

   public static byte[] getServerIdHash(String p_75895_0_, PublicKey p_75895_1_, SecretKey p_75895_2_) {
      try {
         return digestOperation("SHA-1", p_75895_0_.getBytes("ISO_8859_1"), p_75895_2_.getEncoded(), p_75895_1_.getEncoded());
      } catch (UnsupportedEncodingException unsupportedencodingexception) {
         unsupportedencodingexception.printStackTrace();
         return null;
      }
   }

   private static byte[] digestOperation(String p_75893_0_, byte[]... p_75893_1_) {
      try {
         MessageDigest messagedigest = MessageDigest.getInstance(p_75893_0_);

         for(byte[] abyte : p_75893_1_) {
            messagedigest.update(abyte);
         }

         return messagedigest.digest();
      } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
         nosuchalgorithmexception.printStackTrace();
         return null;
      }
   }

   public static PublicKey decodePublicKey(byte[] p_75896_0_) {
      try {
         EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(p_75896_0_);
         KeyFactory keyfactory = KeyFactory.getInstance("RSA");
         return keyfactory.generatePublic(encodedkeyspec);
      } catch (NoSuchAlgorithmException var3) {
      } catch (InvalidKeySpecException var4) {
      }

      LOGGER.error("Public key reconstitute failed!");
      return null;
   }

   public static SecretKey decryptSharedKey(PrivateKey p_75887_0_, byte[] p_75887_1_) {
      return new SecretKeySpec(decryptData(p_75887_0_, p_75887_1_), "AES");
   }

   @OnlyIn(Dist.CLIENT)
   public static byte[] encryptData(Key p_75894_0_, byte[] p_75894_1_) {
      return cipherOperation(1, p_75894_0_, p_75894_1_);
   }

   public static byte[] decryptData(Key p_75889_0_, byte[] p_75889_1_) {
      return cipherOperation(2, p_75889_0_, p_75889_1_);
   }

   private static byte[] cipherOperation(int p_75885_0_, Key p_75885_1_, byte[] p_75885_2_) {
      try {
         return createTheCipherInstance(p_75885_0_, p_75885_1_.getAlgorithm(), p_75885_1_).doFinal(p_75885_2_);
      } catch (IllegalBlockSizeException illegalblocksizeexception) {
         illegalblocksizeexception.printStackTrace();
      } catch (BadPaddingException badpaddingexception) {
         badpaddingexception.printStackTrace();
      }

      LOGGER.error("Cipher data failed!");
      return null;
   }

   private static Cipher createTheCipherInstance(int p_75886_0_, String p_75886_1_, Key p_75886_2_) {
      try {
         Cipher cipher = Cipher.getInstance(p_75886_1_);
         cipher.init(p_75886_0_, p_75886_2_);
         return cipher;
      } catch (InvalidKeyException invalidkeyexception) {
         invalidkeyexception.printStackTrace();
      } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
         nosuchalgorithmexception.printStackTrace();
      } catch (NoSuchPaddingException nosuchpaddingexception) {
         nosuchpaddingexception.printStackTrace();
      }

      LOGGER.error("Cipher creation failed!");
      return null;
   }

   public static Cipher createNetCipherInstance(int p_151229_0_, Key p_151229_1_) {
      try {
         Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
         cipher.init(p_151229_0_, p_151229_1_, new IvParameterSpec(p_151229_1_.getEncoded()));
         return cipher;
      } catch (GeneralSecurityException generalsecurityexception) {
         throw new RuntimeException(generalsecurityexception);
      }
   }
}
