package persistencia;

import java.io.File;
import java.util.Map;

import logica.MovPreflop;

public interface ILectorTablaPreflop {
	public Map<String, MovPreflop> getTabla(File fichero);
}
