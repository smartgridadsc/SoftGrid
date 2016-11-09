package it.illinois.adsc.ema.control;

/**
 * Created by prageethmahendra on 2/3/2016.
 */
public class MultiProcessory {
    public static void main(String[] args) throws Exception {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home")
                + separator + "bin" + separator + "java";
        ProcessBuilder processBuilder =
                new ProcessBuilder(path, "-cp",
                        classpath,
                        Test.class.getName());
        Process process = processBuilder.start();
        process.waitFor();
        System.out.println("process = " + process);
    }
}
