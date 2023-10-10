import com.example.YourProtobufMessage.BInteger; // Import your Protobuf generated classes
import com.google.protobuf.ByteString;

public class Main {
    public static void main(String[] args) {
        long longValue = 37L;

        // Convert the long value to a byte array
        byte[] byteArray = longToBytes(longValue);

        // Create a ByteString from the byte array
        ByteString byteString = ByteString.copyFrom(byteArray);

        // Set the BInteger field with the ByteString directly (without using newBuilder())
        BInteger bInteger = BInteger.newBuilder().setValue(byteString).build();

        // Get the byte array from ByteString
        byte[] retrievedBytes = bInteger.getValue().toByteArray();

        // Print the binary representation of the retrieved bytes
        for (byte b : retrievedBytes) {
            System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1) + " ");
        }
        System.out.println();
    }

    // Convert a long value to a byte array
    private static byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }
}
