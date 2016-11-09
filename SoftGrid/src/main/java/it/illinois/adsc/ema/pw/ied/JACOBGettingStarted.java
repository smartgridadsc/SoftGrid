package it.illinois.adsc.ema.pw.ied;

/**
 * Created by prageethmahendra on 19/1/2016.
 */


        import com.jacob.activeX.ActiveXComponent;
        import com.jacob.com.LibraryLoader;
        import com.jacob.com.Variant;
        import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
        import it.illinois.adsc.ema.pw.ied.pwcom.PWCom;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.InputStream;

/**
 * @author vicky.thakor
 * @date 28th December, 2013
 * http://sourceforge.net/projects/jacob-project/
 * First Program to understand how to use JACOB library
 */
@Deprecated
public class JACOBGettingStarted {

    public static void main(String[] args) {
        PWCom.getInstance().openCase(ConfigUtil.CASE_FILE_NAME);
        PWCom.getInstance().closeCase();
        /**
         * `System.getProperty("os.arch")`
         * It'll tell us on which platform Java Program is executing. Based on that we'll load respective DLL file.
         * Placed under same folder of program file(.java/.class).
         */
        String libFileName = System.getProperty("os.arch").equals("amd64") ? "jcob.dll" : "jacob-1.17-x86.dll";
        try {
            /* Read DLL file*/
            File libFile = new File(libFileName);
            File temporaryDll = File.createTempFile("jacob", ".dll");
            if(libFile.exists()) {
                InputStream inputStream = new FileInputStream(libFile);//String.class.getResourceAsStream(libFile);
                /**
                 *  Step 1: Create temporary file under <%user.home%>\AppData\Local\Temp\jacob.dll
                 *  Step 2: Write contents of `inputStream` to that temporary file.
                 */
                FileOutputStream outputStream = new FileOutputStream(temporaryDll);
                byte[] array = new byte[8192];
                for (int i = inputStream.read(array); i != -1; i = inputStream.read(array)) {
                    outputStream.write(array, 0, i);
                }
                outputStream.close();
                inputStream.close();
            }
            /**
             * `System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());`
             * Set System property same like setting java home path in system.
             *
             * `LibraryLoader.loadJacobLibrary();`
             * Load JACOB library in current System.
             */
            System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());
            LibraryLoader.loadJacobLibrary();

            /**
             * Create ActiveXComponent using CLSID. You can also use program id here.
             * Next line(commented line/compProgramID) shows you how you can create ActiveXComponent using ProgramID.
             */
//            ActiveXComponent compCLSID = new ActiveXComponent("clsid:{00024500-0000-0000-C000-000000000046}");
            ActiveXComponent compCLSID = new ActiveXComponent("pwrworld.SimulatorAuto");
            System.out.println("The Library been loaded, and an activeX component been created");

            /**
            * This is function/method of Microsoft Excel to use it with COM bridge.
            * Excel methods and its use can be found on
            * http://msdn.microsoft.com/en-us/library/bb179167(v=office.12).aspx
            *
            * Understand code:
            * 1. Make Excel visible
            * 2. Get workbook of excel object.
            * 3. Open 1test.xls1 file in excel
            */
            String caseFileName = "C:\\Program Files (x86)\\PowerWorld\\Simulator18\\Sample Cases\\B2OPF.pwb";
//          Dispatch.put(compCLSID, "OpenCase", new Variant(caseFileName));
            Variant returnValue = compCLSID.invoke("OpenCase",caseFileName );
            compCLSID.invoke("CloseCase");

//          Dispatch workbook = compCLSID.getProperty("Workbooks").toDispatch();
//          Dispatch.call(workbook, "Open", new Variant("D:\\test\\test.xls"));

            /* Temporary file will be removed after terminating-closing-ending the application-program */
            temporaryDll.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}