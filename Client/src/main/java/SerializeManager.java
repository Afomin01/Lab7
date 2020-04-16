import java.io.*;

public class SerializeManager {

    public static byte[] toByte(Object object) {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream)) {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteOutputStream.toByteArray();
    }

    public static Object fromByte(byte[] bytes) {
        try {
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
            return objectInputStream.readObject();
        }catch (Exception e){
            Main.outputInfo("Ошибка сереализации команды. Попробуйте еще раз.");
            return null;
        }
    }
}
