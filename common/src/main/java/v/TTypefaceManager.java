package v;

import android.graphics.Typeface;
import com.hello.sandbox.common.util.ContextHolder;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TTypefaceManager {

  private static Map<String, Typeface> TYPEFACE_MAP = new HashMap<>();

  public static Map<String, Typeface> getTypefaceMap() {
    return TYPEFACE_MAP;
  }

  public static void registerTypeFace(String typefaceKey, Typeface typeface) {
    TYPEFACE_MAP.put(typefaceKey, typeface);
  }

  public static void removeTypeFace(String typefaceKey) {
    TYPEFACE_MAP.remove(typefaceKey);
  }

  public static Typeface typefaceFromeAsset(String typefaceKey) {
    if (TYPEFACE_MAP.get(typefaceKey) == null) {
      registerTypeFace(
          typefaceKey,
          Typeface.createFromAsset(
              ContextHolder.context().getResources().getAssets(), typefaceKey));
    }
    return TYPEFACE_MAP.get(typefaceKey);
  }

  public static Typeface typefaceFile(File file) {
    if (TYPEFACE_MAP.get(file.getPath()) == null) {
      registerTypeFace(file.getPath(), Typeface.createFromFile(file));
    }
    return TYPEFACE_MAP.get(file.getPath());
  }
}
