import org.nlogo.api.*;

public class ClipsExtension extends DefaultClassManager {
  public void load(PrimitiveManager primitiveManager) {
    primitiveManager.addPrimitive("first-n-integers", new IntegerList());
    primitiveManager.addPrimitive("clips-sum", new ClipsSum());

    primitiveManager.addPrimitive("create-env", new Clips.CreateEnv());
    primitiveManager.addPrimitive("load", new Clips.Load());
    primitiveManager.addPrimitive("reset", new Clips.Reset());
    primitiveManager.addPrimitive("assert-string", new Clips.AssertString());
    primitiveManager.addPrimitive("run", new Clips.Run());
    primitiveManager.addPrimitive("get-slot-value", new Clips.GetSlotValue());
    primitiveManager.addPrimitive("clear", new Clips.Clear());
    primitiveManager.addPrimitive("destroy", new Clips.Destroy());
  }
}
