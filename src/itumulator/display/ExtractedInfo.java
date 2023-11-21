package itumulator.display;

import java.util.ArrayList;
import java.util.List;

public class ExtractedInfo {
     private Class cl;
     private List<Field> fields;
     private int hashCode;

     public ExtractedInfo(Object o){
          cl = o.getClass();
          hashCode = o.hashCode();
          fields = new ArrayList<>();
          java.lang.reflect.Field[] fields = cl.getDeclaredFields();
          for(java.lang.reflect.Field f : fields){
               boolean isAccessible = f.canAccess(o);
               if(!isAccessible)f.setAccessible(true);
               try {
                    this.fields.add(new Field(f.getName(), f.getType().getName(), f.get(o)));
               } catch (IllegalAccessException e){
               }
               
               if(!isAccessible)f.setAccessible(true);
          }
     }


     class Field {
          String name;
          String type;
          Object value;

          public Field(String name, String type, Object value){
               this.name = name;
               this.type = type;
               this.value = value;
          }

          public String getName(){
               return name;
          }

          public String getType(){
               return type;
          }

          public String getValueAsString(){
               return value.toString();
          }

     }
}
