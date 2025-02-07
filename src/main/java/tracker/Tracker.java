package tracker;

import common.File;
import common.GetFileResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static common.LogWriter.write;

public class Tracker {
    private static final int PORT = 6777;
    private static Integer lastId = 0;
    private static final Map<String, Set<Integer>> peers = new HashMap<>();
    private static final Map<String, File> files = new HashMap<>();
    private static final Map<Integer, String> addresses = new HashMap<>();
    private static final String logFile = "all.txt";

    public static void main(String[] args) {
        write(logFile, "start");
        new Thread(new CLI()).start();
        new Thread(new Manager()).start();
    }

    static void end() {
        logInfo();
        write(logFile, "end");
        System.exit(0);
    }

    static int getPORT() {
        return PORT;
    }

    static Integer getId() {
        write(logFile, "new id " + (lastId + 1));
        return ++lastId;
    }

    static void addAddress(Integer id, String address) {
        addresses.putIfAbsent(id, address);
        logInfo();
    }

    static void endPeer(Integer id) {
        write(logFile, "remove id " + id);
        addresses.remove(id);
        for (String fileName : peers.keySet()) {
            peers.get(fileName).remove(id);
            if (peers.get(fileName).isEmpty()) {
                peers.remove(fileName);
                files.remove(fileName);
            }
        }
        logInfo();
    }

    static void addFile(Integer id, File file) {
        peers.putIfAbsent(file.name(), new HashSet<>());
        peers.get(file.name()).add(id);
        files.putIfAbsent(file.name(), file);

        StringBuilder builder = new StringBuilder().append(file.name()).append(": ");
        for (Integer i : peers.get(file.name()))
            builder.append(i).append(", ");
        write(file.name() + ".txt", builder.append("\n").toString());
        logInfo();
    }

    static GetFileResult getFile(String fileName) {
        Set<Integer> ids = peers.get(fileName);
        File file = files.get(fileName);
        if (ids == null || file == null)
            return GetFileResult.builder().build();

        return GetFileResult.builder()
                .found(true)
                .file(file)
                .addresses(idsToAddresses(ids))
                .build();
    }

    private static Set<String> idsToAddresses(Set<Integer> ids) {
        Set<String> result = new HashSet<>();
        for (Integer id : ids) {
            if (isValid(id))
                result.add(addresses.get(id));
        }
        return result;
    }

    static boolean isValid(Integer id) {
        return addresses.containsKey(id);
    }

    private static void logInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nlastId: ")
                .append(lastId)
                .append("\npeers:\n");
        for (String key : peers.keySet()) {
            builder.append(key).append(": ");
            for (Integer i : peers.get(key))
                builder.append(i).append(", ");
            builder.append("\n");
        }
        builder.append("addresses:\n");
        for (Integer i : addresses.keySet())
            builder.append(i)
                    .append(": ")
                    .append(addresses.get(i))
                    .append(", ");

        write(logFile, builder.append("\n").toString());
    }
}