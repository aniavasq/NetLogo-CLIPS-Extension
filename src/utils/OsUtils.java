package utils;
import java.util.Locale;

public final class OsUtils {
  /**
   * types of Operating Systems
   */
  public enum OsName {
    Windows, MacOS, Linux, Other
  };

  // cached result of OS detection
  protected static OsName osName;

  /**
   * detect the operating system from the os.name System property and cache
   * the result
   * 
   * @returns - the operating system detected
   */
  public static OsName getOsName() {
    if (osName == null) {
      String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
      if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
        osName = OsName.MacOS;
      } else if (OS.indexOf("win") >= 0) {
        osName = OsName.Windows;
      } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
        osName = OsName.Linux;
      } else {
        osName = OsName.Other;
      }
    }
    return osName;
  }
}