import org.nlogo.api.*;

public class ClipsExtension extends DefaultClassManager {
  public void load(PrimitiveManager primitiveManager) {
    primitiveManager.addPrimitive("create-env", new Clips.CreateEnv());
    primitiveManager.addPrimitive("load", new Clips.Load());
    primitiveManager.addPrimitive("reset", new Clips.Reset());
    primitiveManager.addPrimitive("run", new Clips.Run());
    primitiveManager.addPrimitive("clear", new Clips.Clear());
    primitiveManager.addPrimitive("destroy", new Clips.Destroy());
    primitiveManager.addPrimitive("assert-string", new Clips.AssertString());
    primitiveManager.addPrimitive("get-slot-value", new Clips.GetSlotValue());
    primitiveManager.addPrimitive("eval", new Clips.Eval());
  }
}
