import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Reporter;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import net.sf.clipsrules.jni.*;

/**
 * Clips
 */
public class Clips {

  /**
   * ClipsMethod
   */
  public interface ClipsMethod<T> {
    public T getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException;
  }

  /**
   * ClipsCommandReporter
   */
  public static class ClipsCommandReporter<T> {

    Syntax syntax;
    ClipsMethod<T> clipsMethod;
    Context context;

    public void setSyntax(Syntax syntax) {
      this.syntax = syntax;
    }

    public void setClipsMethod(ClipsMethod<T> clipsMethod) {
      this.clipsMethod = clipsMethod;
    }

    public ClipsMethod<T> getClipsMethod() {
      return this.clipsMethod;
    }

    public String getEnvId(Argument[] args) throws LogoException, ExtensionException {
      return args[0].getString();
    }

    public void setContext(Context context) {
      this.context = context;
    }

    public Context getContext() {
      return this.context;
    }
  }

  /**
   * ClipsCommand
   */
  public static class ClipsCommand<T> extends ClipsCommandReporter<T> implements Command {

    @Override
    public Syntax getSyntax() {
      return this.syntax;
    }

    @Override
    public void perform(Argument[] args, Context context) throws ExtensionException {
      String envId = getEnvId(args);
      this.setContext(context);
      try {
        getClipsMethod().getMethod(envId, args);
      } catch (CLIPSException e) {
        e.printStackTrace();
        throw new ExtensionException(e.getMessage());
      }
    }
  }

  /**
   * ClipsReporter
   */
  public static class ClipsReporter<T> extends ClipsCommandReporter<T> implements Reporter {

    @Override
    public Syntax getSyntax() {
      return this.syntax;
    }

    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException {
      String envId = getEnvId(args);
      this.setContext(context);
      try {
        return getClipsMethod().getMethod(envId, args);
      } catch (CLIPSException e) {
        e.printStackTrace();
        throw new ExtensionException(e.getMessage());
      }
    }
  }

  /**
   * CreateEnv
   */
  public static class CreateEnv extends ClipsCommand<Void> {

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType() }, Syntax.VoidType());
    }

    @Override
    public ClipsMethod<Void> getClipsMethod() {
      return new ClipsMethod<Void>() {

        @Override
        public Void getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          Clips.createEnv(envId);
          return null;
        }
      };
    }
  }

  /**
   * Reset
   */
  public static class Reset extends ClipsCommand<Void> {

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType() }, Syntax.VoidType());
    }

    @Override
    public ClipsMethod<Void> getClipsMethod() {
      return new ClipsMethod<Void>() {

        @Override
        public Void getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          Clips.reset(envId);
          return null;
        }
      };
    }
  }

  /**
   * Load
   */
  public static class Load extends ClipsReporter<String> {
    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType(), Syntax.StringType() | Syntax.RepeatableType(), Syntax.StringType() | Syntax.RepeatableType() }, Syntax.StringType(), 1, 2);
    }
    
    @Override
    public ClipsMethod<String> getClipsMethod() {
      return new ClipsMethod<String>() {

        @Override
        public String getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          String fileName = "";
          String dirName = "";
          try {
            fileName = args[1].getString();
            dirName = args[2].getString();
          } catch (Exception e) {
            dirName = Clips.getRulesDir(Load.this.getContext());
          }

          if (fileName.equals("")) {
            File dir = new File(dirName);
            for (File file : dir.listFiles()) {
              if (file.getName().endsWith((".clp"))) {
                Clips.load(envId, file.getAbsolutePath());
              }
            }
            return dirName;
          } else {
            fileName = dirName + "/" + fileName;
            Clips.load(envId, fileName);
            return fileName;
          }
        }
      };
    }
  }

  /**
   * AssertString
   */
  public static class AssertString extends ClipsReporter<String> {

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType(), Syntax.StringType() }, Syntax.StringType());
    }
    
    @Override
    public ClipsMethod<String> getClipsMethod() {
      return new ClipsMethod<String>() {

        @Override
        public String getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          String facString = args[1].getString();

          return Clips.assertString(envId, facString).toString();
        }
      };
    }    
  }

  /**
   * Run
   */
  public static class Run extends ClipsCommand<Void> {

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType(), Syntax.NumberType() | Syntax.RepeatableType() }, Syntax.StringType(), 1, 1);
    }
    
    @Override
    public ClipsMethod<Void> getClipsMethod() {
      return new ClipsMethod<Void>() {

        @Override
        public Void getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          long runLimit;
          try {
            runLimit = args[1].getIntValue();
            Clips.run(envId, runLimit);
          } catch (Exception e) {
            Clips.run(envId);
          }
          return null;
        }
      };
    }    
  }

  /**
   * Clear
   */
  public static class Clear extends ClipsCommand<Void> {

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType() }, Syntax.VoidType());
    }
    
    @Override
    public ClipsMethod<Void> getClipsMethod() {
      return new ClipsMethod<Void>() {

        @Override
        public Void getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          Clips.clear(envId);
          return null;
        }
      };
    }    
  }

  /**
   * Destroy
   */
  public static class Destroy extends ClipsCommand<Void> {

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType(), Syntax.BooleanType() | Syntax.RepeatableType() }, Syntax.VoidType(), 1, 1);
    }
    
    @Override
    public ClipsMethod<Void> getClipsMethod() {
      return new ClipsMethod<Void>() {

        @Override
        public Void getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          boolean throwErrors;
          try {
            throwErrors = args[1].getBooleanValue();
          } catch (Exception e) {
            throwErrors = false;
          }
          Clips.destroy(envId, throwErrors);
          return null;
        }
      };
    }    
  }

  public static class FinFact extends ClipsReporter<String> {
    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType(), Syntax.StringType(), Syntax.StringType(), Syntax.StringType() |  Syntax.RepeatableType(), Syntax.StringType(), Syntax.StringType() |  Syntax.RepeatableType() }, Syntax.StringType(), 3, 3);
    }

    public FactAddressValue getFactAddressValue(String envId, String deftemplate) throws CLIPSException {
      return Clips.findFact(envId, deftemplate);
    }

    public FactAddressValue getFactAddressValue(String envId, String deftemplate, String variable, String condition) throws CLIPSException {
      return Clips.findFact(envId, variable, deftemplate, condition);
    }

    @Override
    public ClipsMethod<String> getClipsMethod() {
      return new ClipsMethod<String>() {

        @Override
        public String getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          String deftemplate = args[1].getString();
      
          String variable;
          String condition;

          FactAddressValue fv;

          try {
            variable = args[2].getString();
            condition = args[3].getString();

            fv = getFactAddressValue(envId, variable, deftemplate, condition);
          } catch (Exception e) {
            fv = getFactAddressValue(envId, deftemplate);
          }

          try {
            return fv + "";
          } catch (NullPointerException e) {
            return "FALSE";
          }
        }
      };
    }
  }
  /**
   * GetSlotValue
   */
  public static class GetSlotValue extends FinFact {
    
    @Override
    public ClipsMethod<String> getClipsMethod() {
      return new ClipsMethod<String>() {

        @Override
        public String getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          String deftemplate = args[1].getString();
          String slotName = args[2].getString();
          String variable;
          String condition;

          FactAddressValue fv;

          try {
            variable = args[3].getString();
            condition = args[4].getString();

            fv = getFactAddressValue(envId, variable, deftemplate, condition);
          } catch (Exception e) {
            fv = getFactAddressValue(envId, deftemplate);
          }

          try {
            return fv.getSlotValue(slotName) + "";
          } catch (NullPointerException e) {
            return "FALSE";
          }
        }
      };
    }
  }


  private static HashMap<String, Environment> environments = new HashMap<>();

  private static String rulesDir;

  public static Environment createEnv(String envId) {
    Environment clips = new Environment();
    environments.put(envId, clips);
    return clips;
  }

  public static Environment getEnv(String envId) throws CLIPSException {
    Environment env =  environments.get(envId);
    if (env != null) {
      return env;
    } else {
      throw new CLIPSException("Environment < " + envId + " > does not exist.");
    }
  }

  public static Environment removeEnv(String envId) {
    return environments.remove(envId);
  }

  public static void destroyEnv(String envId) {
    Environment clips = environments.remove(envId);
    clips.destroy();
  }

  public static void setRulesDir(String rulesDir) {
    Clips.rulesDir = rulesDir;
  }

  public static String getRulesDir() {
    return Clips.rulesDir;
  }

  public static String getRulesDir(Context context) {
    return context.workspace().getModelDir() + "/rules";
  }

  public static void reset(String envId) throws CLIPSException {
    Environment clips = getEnv(envId);
    clips.reset();
  }

  public static void load(String envId, String fileName) throws CLIPSException {
    Environment clips = getEnv(envId);
    clips.load(fileName);
  }

  public static long run(String envId) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.run();
  }

  public static long run(String envId, long runLimit) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.run(runLimit);
  }

  public static void clear(String envId) throws CLIPSException {
    Environment clips = getEnv(envId);
    clips.clear();
  }

  public static void destroy(String envId, boolean throwErrors) throws CLIPSException {
    Environment clips = getEnv(envId);
    clips.destroy(throwErrors);
    removeEnv(envId);
  }

  public static FactAddressValue assertString(String envId, String factString) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.assertString(factString);
  }

  public static InstanceAddressValue makeInstance(String envId, String instanceStr) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.makeInstance(instanceStr);
  }

  public static FactAddressValue findFact(String envId, String variable, String deftemplate, String condition) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findFact(variable, deftemplate, condition);
  }

  public static FactAddressValue findFact(String envId, String deftemplate) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findFact(deftemplate);
  }

  public static List<FactAddressValue> findAllFacts(String envId, String variable, String deftemplate, String condition) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findAllFacts(variable, deftemplate, condition);
  }

  public static List<FactAddressValue> findAllFacts(String envId, String deftemplate) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findAllFacts(deftemplate);
  }
}
