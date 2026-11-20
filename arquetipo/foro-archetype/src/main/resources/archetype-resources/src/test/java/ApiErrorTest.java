#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};
import ${package}.error.ApiError;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ApiErrorTest {
 @Test void construyeError(){var e=ApiError.builder().status(404).error("No encontrado").build();assertEquals(404,e.status());assertNotNull(e.timestamp());}
 @Test void rechazaEstadoExitoso(){assertThrows(IllegalArgumentException.class,()->ApiError.builder().status(200).error("x").build());}
 @Test void detallesSonInmutables(){var m=new HashMap<String,String>();m.put("campo","email");var b=ApiError.builder().status(400).error("x").details(List.of(m));m.clear();var e=b.build();assertEquals("email",e.details().get(0).get("campo"));assertThrows(UnsupportedOperationException.class,()->e.details().get(0).clear());}
}
