import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.api.Reporter;
import org.nlogo.core.LogoList;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import net.sf.clipsrules.jni.*;

/**
 * Esta es la clase que funciona como fachada para poder usar las funciones y
 * procedimientos de la interfaz nativa de Java de CLIPS (CLIPS JNI). CLIPS JNI
 * es la interfaz para poder usar la funcionalidad de CLIPS para desarrollar
 * aplicaciones de Java.
 * 
 * @author Aníbal Vásquez C.
 * @version %I %G
 */
public class Clips {

  /**
   * Esta interfaz de Java permite implementar métodos en el JNI de CLIPS.
   * @param <T> Objeto que debe representar el tipo de dato primitivo del método.
   */
  public interface ClipsMethod<T> {
    /**
     * Implementación del uso de una función o procedimiento de CLIPS que se
     * invoca mediante un método del JNI.
     * 
     * @param envId Id. del entorno de CLIPS que debe ejecutar el método.
     * @param args Lista de argumentos usados por la función o procedimiento
     * que implementa el método del JNI de CLIPS.
     * @return puede retornar o no el valor de la función o procedimiento de
     * CLIPS.
     * @throws CLIPSException
     * @throws ExtensionException
     */
    public T getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException;
  }

  /**
   * Clase interna que permite definir un <i>reporter</i> o <i>command</i> de
   * NetLogo de tipo T.
   * @param <T> T es la representación del tipo de dato primitivo que podrá
   * soportar el <i>reporter</i> o <i>command</i>.
   */
  public static class ClipsCommandReporter<T> {

    Syntax syntax;
    ClipsMethod<T> clipsMethod;
    Context context;

    /**
     * Setter que configura la sintaxis del <i>reporter</i> o <i>command</i>.
     * @param syntax Estructura de datos que representa la sintaxis.
     */
    public void setSyntax(Syntax syntax) {
      this.syntax = syntax;
    }

    /**
     * Setter que configura el método del Entorno de CLIPS vinculado.
     * @param clipsMethod
     */
    public void setClipsMethod(ClipsMethod<T> clipsMethod) {
      this.clipsMethod = clipsMethod;
    }

    /**
     * Getter que permite obtener el método del entorno de CLIPS vinculado.
     * @return El entorno método de CLIPS vinculado al <i>reporter</i> o 
     * <i>command</i>.
     */
    public ClipsMethod<T> getClipsMethod() {
      return this.clipsMethod;
    }

    /**
     * Getter que retorna el id. del entorno vinculado al método de CLIPS
     * implementado.
     * 
     * @param args Lista de argumentos para poder obtener el id del entorno de CLIPS.
     * @return Id. del Entorno de CLIPS.
     * @throws LogoException
     * @throws ExtensionException
     */
    public String getEnvId(Argument[] args) throws LogoException, ExtensionException {
      return args[0].getString();
    }

    /**
     * Setter que establece el contexto de NetLogo para ser usado.
     * @param context Contexto del proceso de NetLogo.
     */
    public void setContext(Context context) {
      this.context = context;
    }

    /**
     * Getter del contexto del proceso de NetLogo vinculado a un método de CLIPS.
     * @return El contexto del proceso de NetLogo.
     */
    public Context getContext() {
      return this.context;
    }
  }

  /**
   * Clase interna para definir un <i>command<i> de NetLogo de tipo <b>T</b>.
   * La mayor diferencia entre un <i>command</i> y un <i>reporter</i> es que el
   * <i>command</i> no retorna un valor.
   * 
   * @param <T> T es la representación del tipo de dato primitivo que podrá
   * soportar el <i>command</i> de NetLogo. Este es heredado por la clase
   * ClipsCommandReporter pero al ser un <i>command</i> T debe ser Void.
   */
  public static class ClipsCommand<T> extends ClipsCommandReporter<T> implements Command {

    /**
     * Getter de la sintaxis del <i>command</i>.
     * @return La sintaxis que usa.
     */
    @Override
    public Syntax getSyntax() {
      return this.syntax;
    }

    /**
     * Realiza la ejecución del método de CLIPS, que a su vez implementa la
     * función o procedimiento de la librería de CLIPS.
     * 
     * @param args Lista de los parámetros necesarios para ejecutar el
     * <i>command</i> de Netlogo que se vincula a la funcionalidad de CLIPS.
     * @param context El contexto del proceso de NetLogo.
     * @throws ExtensionException
     */
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
   * Clase interna para definir un <i>reporter<i> de NetLogo de tipo <b>T</b>.
   * La mayor diferencia entre un <i>command</i> y un <i>reporter</i> es que el
   * <i>reporter</i> debe retornar un valor.
   * 
   * @param <T> T es la representación del tipo de dato primitivo que podrá
   * soportar el <i>reporter</i> de NetLogo.
   */
  public static class ClipsReporter<T> extends ClipsCommandReporter<T> implements Reporter {

    /**
     * Getter de la sintaxis del <i>reporter</i>.
     * @return La sintaxis que usa.
     */
    @Override
    public Syntax getSyntax() {
      return this.syntax;
    }

    /**
     * 
     * @param args Lista de los parámetros necesarios para ejecutar el
     * <i>reporter</i> de Netlogo que se vincula a la funcionalidad de CLIPS.
     * @param context El contexto del proceso de NetLogo.
     * @return El valor primitivo a reportar en el entorno de NetLogo.
     * @throws ExtensionException
     */
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
   * Clase que interna que implementa el <i>command</i> que permite crear
   * un entorno de CLIPS desde NetLogo.
   */
  public static class CreateEnv extends ClipsCommand<Void> {

    
    /**
     * Implementación de la función que permite obtener la sintaxis del
     * <i>command</i>.
     * 
     * @return La sintaxis específica del <i>command</i>. La sintaxis en este
     * caso es: (string) -> void. Es decir que, recibe un string, que es el
     * nombre del entorno, como parámetro y no retorna valor.
     */
    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType() }, Syntax.VoidType());
    }

    /**
     * Implementación de la ejecución del método del <i>facade</i>.
     * 
     * @return El método que vincula la funcionalidad de CLIPS es retornado
     * para ser usado por el método <i>perform</i>. En este caso el método
     * es el que permite crear un nuevo entorno de CLIPS con un string el cual
     * será el id del entorno.
     */
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

  /**
   * FindFact
   */
  public static class FindFact extends ClipsReporter<String> {
    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType(), Syntax.StringType(), Syntax.StringType() |  Syntax.RepeatableType(), Syntax.StringType(), Syntax.StringType() |  Syntax.RepeatableType() }, Syntax.StringType(), 2, 2);
    }

    public FactAddressValue getFactAddressValue(String envId, String deftemplate) throws CLIPSException {
      return Clips.findFact(envId, deftemplate);
    }

    public FactAddressValue getFactAddressValue(String envId, String variable, String deftemplate, String condition) throws CLIPSException {
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
  public static class GetSlotValue extends FindFact {

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType(), Syntax.StringType(), Syntax.StringType(), Syntax.StringType() |  Syntax.RepeatableType(), Syntax.StringType(), Syntax.StringType() |  Syntax.RepeatableType() }, Syntax.StringType(), 3, 3);
    }
    
    @Override
    public ClipsMethod<String> getClipsMethod() {
      return new ClipsMethod<String>() {

        @Override
        public String getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          String deftemplate = args[1].getString();
          String slotName = args[2].getString();
          String variable = "";
          String condition = "";

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

  /**
   * FindAllFact
   */
  public static class FindAllFacts extends ClipsReporter<LogoList> {
    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType(), Syntax.StringType(), Syntax.StringType(), Syntax.StringType() |  Syntax.RepeatableType(), Syntax.StringType(), Syntax.StringType() |  Syntax.RepeatableType() }, Syntax.ListType(), 3, 3);
    }

    public List<FactAddressValue> getFacts(String envId, String deftemplate) throws CLIPSException {
      return Clips.findAllFacts(envId, deftemplate);
    }

    public List<FactAddressValue> getFacts(String envId, String variable, String deftemplate, String condition) throws CLIPSException {
      return Clips.findAllFacts(envId, variable, deftemplate, condition);
    }

    @Override
    public ClipsMethod<LogoList> getClipsMethod() {
      return new ClipsMethod<LogoList>() {

        @Override
        public LogoList getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          String deftemplate = args[1].getString();
          String variable;
          String condition;
          LogoListBuilder list = new LogoListBuilder();

          try {
            variable = args[2].getString();
            condition = args[3].getString();
            list.addAll(getFacts(envId, variable, deftemplate, condition));
          } catch (Exception e) {
            list.addAll(getFacts(envId, deftemplate));
          }

          try {
            return list.toLogoList();
          } catch (NullPointerException e) {
            list.add("FALSE");
            return list.toLogoList();
          }
        }
      };
    }
  }

  /**
   * GetAllSlotValues
   */
  public static class GetAllSlotValues extends FindAllFacts {
    @Override
    public ClipsMethod<LogoList> getClipsMethod() {
      return new ClipsMethod<LogoList>() {

        @Override
        public LogoList getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          String deftemplate = args[1].getString();
          String slotName = args[2].getString();
          String variable;
          String condition;
          LogoListBuilder list = new LogoListBuilder();
          List<FactAddressValue> fvs;

          try {
            variable = args[3].getString();
            condition = args[4].getString();
            fvs = getFacts(envId, variable, deftemplate, condition);
          } catch (Exception e) {
            fvs = getFacts(envId, deftemplate);
          }

          for(FactAddressValue fv:fvs) {
            list.add(fv.getSlotValue(slotName) + "");
          }

          try {
            return list.toLogoList();
          } catch (NullPointerException e) {
            list.add("FALSE");
            return list.toLogoList();
          }
        }
      };
    }
  }

  /**
   * Eval
   */
  public static class Eval extends ClipsReporter<String> {
    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType(), Syntax.StringType() }, Syntax.StringType());
    }

    @Override
    public ClipsMethod<String> getClipsMethod() {
      return new ClipsMethod<String>() {

        @Override
        public String getMethod(String envId, Argument[] args) throws CLIPSException, ExtensionException {
          String evalString = args[1].getString();
          return Clips.eval(envId, evalString).toString();
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
    Environment env = environments.get(envId);
    if (env != null) {
      return env;
    } else {
      throw new CLIPSException("Environment <" + envId + "> does not exist.");
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

  public static FactAddressValue findFact(String envId, String variable, String deftemplate, String condition)
      throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findFact(variable, deftemplate, condition);
  }

  public static FactAddressValue findFact(String envId, String deftemplate) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findFact(deftemplate);
  }

  public static List<FactAddressValue> findAllFacts(String envId, String variable, String deftemplate, String condition)
      throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findAllFacts(variable, deftemplate, condition);
  }

  public static List<FactAddressValue> findAllFacts(String envId, String deftemplate) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findAllFacts(deftemplate);
  }

  public static InstanceAddressValue findInstance(String envId, String defclass) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findInstance(defclass);
  }

  public static InstanceAddressValue findInstance(String envId, String variable, String defclass, String condition)
      throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findInstance(variable, defclass, condition);
  }

  public static List<InstanceAddressValue> findAllInstance(String envId, String defclass) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findAllInstances(defclass);
  }

  public static List<InstanceAddressValue> findAllInstance(String envId, String variable, String defclass, String condition)
      throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.findAllInstances(variable, defclass, condition);
  }

  public static PrimitiveValue eval(String envId, String evalString) throws CLIPSException {
    Environment clips = getEnv(envId);
    return clips.eval(evalString);
  }
}
