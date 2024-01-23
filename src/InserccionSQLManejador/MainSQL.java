package InserccionSQLManejador;
import java.sql.*;

public class MainSQL {

	private static Connection crearConexion() {
		String usr = "root";
		String pass = "admin";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String urlCon = "jdbc:mysql://localhost:3306/reservas";
			Connection con = DriverManager.getConnection(urlCon, usr, pass);
			
			System.out.println("Conexión creada");

			return con;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}

	public static void ejecutarComando(String consulta) throws SQLException {
		Connection con = crearConexion();
		Statement st = con.createStatement();
		int result = st.executeUpdate(consulta);
		System.out.println("Comando ejecutado");
		
		cerrarConexion(st, con);
	}

	private static void cerrarConexion(Statement st, Connection con) throws SQLException {
		st.close();
		con.close();
		System.out.println("Conexión cerrada");
	}
}
