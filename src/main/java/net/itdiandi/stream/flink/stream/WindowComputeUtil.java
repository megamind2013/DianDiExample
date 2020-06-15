package net.itdiandi.stream.flink.stream;

public class WindowComputeUtil {
    public static long myGetWindowStartWithOffset(long timestamp, long offset, long windowSize) {
        return timestamp - (timestamp - offset + windowSize) % windowSize;
    }
}
