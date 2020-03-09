import java.io.File;
import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import net.sf.clipsrules.jni.*;


public class ClipsSum implements Reporter {

  @Override
  public Syntax getSyntax() {
    return SyntaxJ.reporterSyntax(new int[] { Syntax.NumberType(), Syntax.NumberType() }, Syntax.StringType());
  }

  @Override
  public Object report(Argument[] args, Context context) throws ExtensionException {
    int a = args[0].getIntValue(), b = args[1].getIntValue();
    String rulesDir = context.workspace().getModelDir() + "/rules";

    try {
      Clips.createEnv("X");
      File dir = new File(rulesDir);
      for (File file : dir.listFiles()) {
        if (file.getName().endsWith((".clp"))) {
          Clips.load("X", file.getAbsolutePath());
        }
      }
      Clips.reset("X");
      Clips.assertString("X", "(vals " + a + " " + b + ")");
      Clips.run("X");

      FactAddressValue fv = Clips.findFact("X", "sum");
      String res = "" + fv.getSlotValue("total");
      return res;
    } catch (CLIPSException e) {
      e.printStackTrace();
      throw new ExtensionException(e.getMessage());
    }
  }
}