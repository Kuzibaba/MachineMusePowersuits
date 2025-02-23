package numina;

import lehjr.numina.common.base.NuminaLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * list resources available from the classpath @ *
 */
public class ResourceList {
    //https://stackoverflow.com/questions/3923129/get-a-list-of-resources-from-classpath-directory
//    /**
//     * for all elements of java.class.path get a Collection of resources Pattern
//     * pattern = Pattern.compile(".*"); gets all resources
//     *
//     * @param pattern
//     *            the pattern to match
//     * @return the resources in the order they are found
//     */
//    public static Collection<String> getResources(final Pattern pattern){
//        final ArrayList<String> retval = new ArrayList<>();
//        final String classPath = System.getProperty("java.class.path", ".");
//        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
//        for(final String element : classPathElements){
//            retval.addAll(getResources(element, pattern));
//        }
//        return retval;
//    }

    public static ArrayList<File> getResources(final String element, final Pattern pattern) {
        NuminaLogger.logDebug("element: " + element);
        return  getResources(new File(element), pattern);
    }

    public static ArrayList<File> getResources(final File file, final Pattern pattern) {
        final ArrayList<File> retval = new ArrayList<>();
        if (file.exists()) {
            if (file.isDirectory()) {
                NuminaLogger.logDebug("is directory");
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else {
                NuminaLogger.logDebug("is jar");
                retval.addAll(getResourcesFromJarFile(file, pattern));
            }
        } else {
            NuminaLogger.logDebug("does not exist");
        }
        NuminaLogger.logDebug("found " + retval.size() + " files");
        return retval;
    }

    private static ArrayList<File> getResourcesFromJarFile(final File file, final Pattern pattern){
        final ArrayList<File> retval = new ArrayList<>();
        ZipFile zf;
        try{
            zf = new ZipFile(file);
        } catch(final ZipException e){
            throw new Error(e);
        } catch(final IOException e){
            throw new Error(e);
        }
        final Enumeration e = zf.entries();
        while(e.hasMoreElements()){
            final ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = ze.getName();
            // changed to find if contains a string vs matching entire string
            final boolean accept = pattern.matcher(fileName).find();//.matches();
            if(accept){
                retval.add(new File(fileName));
            }
        }
        try{
            zf.close();
        } catch(final IOException e1){
            throw new Error(e1);
        }
        return retval;
    }

    public static ArrayList<File> getResourcesFromDirectory(final File directory, final Pattern pattern){
        final ArrayList<File> retval = new ArrayList<>();
        final File[] fileList = directory.listFiles();
        for(final File file : fileList){
            if(file.isDirectory()){
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else{
                try{
                    final File fileName = file.getCanonicalFile();
                    // changed to find if contains a string vs matching entire string
                    final boolean accept = pattern.matcher(fileName.getCanonicalPath()).find();//.matches();
                    NuminaLogger.logDebug("pattern matches filename <" + fileName + ">: " + accept);
                    if(accept){
                        retval.add(fileName);
                    }
                } catch(final IOException e){
                    throw new Error(e);
                }
            }
        }
        return retval;
    }

//    /**
//     * list the resources that match args[0]
//     *
//     * @param args
//     *            args[0] is the pattern to match, or list all resources if
//     *            there are no args
//     */
//    public static void main(final String[] args){
//        Pattern pattern;
//        if(args.length < 1){
//            pattern = Pattern.compile(".*");
//        } else{
//            pattern = Pattern.compile(args[0]);
//        }
//        final Collection<String> list = ResourceList.getResources(pattern);
//        for(final String name : list){
//            NuminaLogger.logDebug(name);
//        }
//    }
}
