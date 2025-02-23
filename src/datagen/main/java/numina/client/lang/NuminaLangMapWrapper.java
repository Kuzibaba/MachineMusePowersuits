package numina.client.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lehjr.numina.common.base.NuminaLogger;
import net.minecraft.data.CachedOutput;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class NuminaLangMapWrapper {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    String locale;
    Map<String, String> data = new HashMap<>();
    Map<String, String> extraData = new HashMap<>();
    NuminaLangMapWrapper defaultLangWrapper;

    public NuminaLangMapWrapper(File jsonFile) {
        defaultLangWrapper =this;
        loadAndParse(jsonFile);
    }

    public NuminaLangMapWrapper(File jsonFile, NuminaLangMapWrapper defaultLangWrapper) {
        this.defaultLangWrapper = defaultLangWrapper;
        loadAndParse(jsonFile);
    }

    /**
     * Called by other instances to evaluate where to put the translation key and translation
     * @param key
     * @return
     */
    public boolean defaultMapHasKey(String key) {
        if (defaultLangWrapper == this) {
            return data.containsKey(key);
        }
        return defaultLangWrapper.defaultMapHasKey(key);
    }

    public Set<Map.Entry<String, String>> getDefaultMapEntries() {
        if (thisIsDefault()) {
            return data.entrySet();
        }
        return defaultLangWrapper.getDefaultMapEntries();
    }

    public void addEntryToMap(Map.Entry<String, String> entry) {
        if (defaultLangWrapper == this) {
            data.put(entry.getKey(), entry.getValue());
        }

        if (defaultMapHasKey(entry.getKey())) {
            data.put(entry.getKey(), entry.getValue());
        } else {
            extraData.put(entry.getKey(), entry.getValue());
        }
    }

    boolean thisIsDefault () {
        return defaultLangWrapper == this;
    }

    public void loadAndParse(File jsonFile) {
        this.locale = FilenameUtils.getBaseName(jsonFile.getName());
        NuminaLogger.logDebug("parsing file: " + jsonFile.getName());
        NuminaLogger.logDebug("this is default: " + thisIsDefault());

        Map<String, String> map = JSonLoader.parseJsonFile(jsonFile);
        NuminaLogger.logDebug("map size: " + map.size());


        map.entrySet().stream().filter(entry -> !entry.getKey().equals("_comment"))
                .forEach(entry -> addEntryToMap(entry));
        NuminaLogger.logDebug("data size: " + data.size());
        NuminaLogger.logDebug("extra data size: " + extraData.size());

    }

    public void savetoOutputFolder(CachedOutput cache, Path outputFolder) {
        try {
            if (!thisIsDefault()) {
                if (!extraData.isEmpty()) {
                    File extraFile = new File(outputFolder.toFile(), locale + "_extra.json");
                    save(cache, maptoJsonObject(extraData), extraFile.toPath(), true);
                }

                Map<String, String> missingMap = new TreeMap<>();
                getDefaultMapEntries().stream().filter(thing -> !thing.getKey().equals("_comment")).forEach(entry ->{
                    if (!data.containsKey(entry.getKey())) {
                        missingMap.put(entry.getKey(), entry.getValue());
                    }
                });

                if (!missingMap.isEmpty()) {
                    missingMap.forEach((k, v) -> data.putIfAbsent(k, v));
                    File missingFile = new File(outputFolder.toFile(), locale + "_missing.json");
                    save(cache, maptoJsonObject(missingMap), missingFile.toPath(), true);
                }
            }

            if (!data.isEmpty()) {
                File outFile = new File(outputFolder.toFile(), locale + ".json");
                save(cache, maptoJsonObject(data), outFile.toPath(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JsonObject maptoJsonObject(Map<String, String> map) {
        JsonObject jsonObject = new JsonObject();
        map.entrySet().forEach(entry -> jsonObject.addProperty(entry.getKey(), entry.getValue()));
        return jsonObject;
    }

    private void save(CachedOutput cache, Object object, Path target, boolean overwrite) throws IOException {
//        if (locale.startsWith("zh_")) {
            fileWriter(cache, object, target, overwrite);
//        } else {
//            String data = GSON.toJson(object);
//            data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data); // Escape unicode after the fact so that it's not double escaped by GSON
//
//            String hash = IDataProvider.SHA1.hashUnencodedChars(data).toString();
//            if (!Objects.equals(cache.getHash(target), hash) || (!Files.exists(target) || overwrite)) {
//                Files.createDirectories(target.getParent());
//
//                try (BufferedWriter bufferedwriter = Files.newBufferedWriter(target)) {
//                    bufferedwriter.write(data);
//                }
//            }
//            cache.putNew(target, hash);
//        }
    }


    public void fileWriter(CachedOutput cache, Object object, Path target, boolean overwrite) {
        String dataOut = GSON.toJson(object);
//        String hash = DataProvider..f_123918_.hashUnencodedChars(dataOut).toString();
//        try {
//            Files.createDirectories(target.getParent());
//            if (overwrite || !target.toFile().exists()) {
//                FileWriter fileWriter = new FileWriter(target.toFile());
//                fileWriter.write(dataOut);
//                fileWriter.flush();
//                fileWriter.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        cache.m_123940_(target, hash);
    }
}

