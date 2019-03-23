import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.JsonUtils;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class JsonInserter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        File directory = new File(scanner.nextLine().replace("/","\\"));
        for (File file : Objects.requireNonNull(directory.listFiles((dir, name) -> name.endsWith(".json")))) {
            try (FileReader reader = new FileReader(file);
                 BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
            ) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                //网友推荐更加简洁的写法
                while ((line = br.readLine()) != null) {
                    // 一次读入一行数据
                    stringBuilder.append(line+"\n");
                }
                stringBuilder.insert(4,"\"craftTire\": 0,\n  ");
                try(FileWriter outputStream = new FileWriter(file)) {
                    outputStream.write(stringBuilder.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
