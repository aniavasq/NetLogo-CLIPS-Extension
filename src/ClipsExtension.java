import java.util.Vector;
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
    primitiveManager.addPrimitive("find-fact", new Clips.FindFact());
    primitiveManager.addPrimitive("get-slot-value", new Clips.GetSlotValue());
    primitiveManager.addPrimitive("eval", new Clips.Eval());
  }

  @Override
  public void unload(ExtensionManager em) {
    /**
     * Porción de código para hacer unload de las librerías nativas, así la 
     * si la extensión es cargada de nuevo en el mismo entorno de NetLogo, no
     * causará problemas.
     * 
     * -- Tomado de Arduino Extension --
     * -- https://github.com/NetLogo/Arduino-Extension/blob/hexy/src/main/java/ArduinoExtension.java --
     */
    try
    {
      ClassLoader classLoader = this.getClass().getClassLoader() ;
      java.lang.reflect.Field field = ClassLoader.class.getDeclaredField( "nativeLibraries" ) ;
      field.setAccessible(true);
      @SuppressWarnings("unchecked")
      Vector<Object> libs = (Vector<Object>) (field.get(classLoader)) ;
      for ( Object o : libs )
      {
        java.lang.reflect.Method finalize = o.getClass().getDeclaredMethod( "finalize" , new Class<?>[0] ) ;
        finalize.setAccessible( true ) ;
        finalize.invoke( o , new Object[0] ) ;
      }
    } catch(Exception e ) {
      System.err.println( e.getMessage() ) ;
    }
  }
}
