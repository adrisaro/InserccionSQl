package InserccionSQLManejador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String nombreFichero = "";
		Scanner sc = new Scanner(System.in);
		String[] listaTiposAdmitidos = {"nombre","fechaEvento","tipo","asistentes","tipoCocina","numeroJornadas","habitaciones","tipoMesa","comensalesMesa"};
		ArrayList<String[]> listaRelaciones = new ArrayList<String[]>();
		
		Boolean esXml = false;
		int salida= 0; //0 = xml, 1 = json, 2 = SQL
		
		System.out.println("Seleccione su formato de salida: \n1: XML\n2:JSON \n3: SQL");
		nombreFichero = sc.nextLine();
		if(nombreFichero.equals("1")) salida = 0; else if(nombreFichero.equals("2")) salida = 1; else if (nombreFichero.equals("3")) salida = 2; else System.out.println("Error: Escriba 1 o 2.");
		System.out.println("Diga el nombre del archivo a introducir");
		nombreFichero = sc.nextLine();


		try {
			BufferedReader br = new BufferedReader(new FileReader(nombreFichero));
			String linea = br.readLine();
			
			if(linea.startsWith("<")) esXml = true;
			else if(linea.startsWith("{")) esXml = false;
			
			String[] valores = {"",""};
			while (linea != null) {
				boolean puerta = false;
				
				valores = extraerInfo(linea, esXml);

				for (String s : listaTiposAdmitidos) {
					if(s.equalsIgnoreCase(valores[0])) puerta = true;
				}
				if(puerta && !valores[1].equals("")) {
					listaRelaciones.add(valores);
				}
				linea = br.readLine();
			}
			
			if(salida != 2) {
				System.out.println("Introduzca como desea que se llame el fichero resultado.");
				nombreFichero = sc.nextLine();
			}
			
			if(salida == 1)  {
				BufferedWriter bw = new BufferedWriter(new FileWriter(nombreFichero+".json"));
				bw.write("{\n"+"\"reserva\": {\n");
				escribirJson(listaRelaciones, bw);
			} else if (salida == 0){
				BufferedWriter bw = new BufferedWriter(new FileWriter(nombreFichero+".xml"));
				bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+"<reserva>"+"\n");
				escribirXml(listaRelaciones, bw);
			} else if (salida == 2) {
				escribirSentenciaSQL(listaRelaciones);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String[] extraerInfo(String linea, boolean esXml) {
		
		String[] valores = {"",""};
		if(esXml) {
			String[] partesLinea = linea.split("[><]");
			valores[0] = partesLinea[1];
			if(partesLinea[1].contains("xml") || partesLinea[1].contains("reserva")) {
				partesLinea[1] = "";
				valores[0] = "";
				valores[1] = "";
			} 
			if (linea.contains(partesLinea[1]) && !partesLinea[1].equals("")){
				valores[1] = partesLinea[2];
			} else {
				valores[1] = "";
			}
		} else { //SI ES UN JSON
			linea.trim();
			if(!linea.contains("{") && !linea.contains("}")) {
				String[] partesLinea = linea.split("\"");
				valores[0] = partesLinea[1];
				valores[1] = partesLinea[3]; 
			} 
		}
		return valores;
	}
	
	public static void escribirJson(ArrayList<String[]> listaRelaciones, BufferedWriter bw) throws IOException {
		for(int i = 0; i < listaRelaciones.size(); i++) {
			String[] partes = listaRelaciones.get(i);
			String dolor = "\""+partes[0]+"\":\""+partes[1]+"\"\n";
			bw.write(dolor);
		}
		bw.write("	}\n}");
		bw.close();
	}
	public static void escribirXml(ArrayList<String[]> listaRelaciones, BufferedWriter bw) throws IOException {
		for(int i = 0; i < listaRelaciones.size(); i++) {
			String[] partes = listaRelaciones.get(i);
			String dolor = "<"+partes[0]+">"+partes[1]+"</"+partes[0]+">\n";
			bw.write(dolor);
		}
		bw.write("</reserva>");
		bw.close();
	}
	public static void escribirSentenciaSQL(ArrayList<String[]> listaRelaciones) throws SQLException {
		String[] camposTabla = {"","","","","","",""}; //No incluye idReservas
		
		for(int i = 0; i < listaRelaciones.size(); i++) {
			String[] partes = listaRelaciones.get(i);
			camposTabla[i] = partes[1];
		}
	
		String consulta = "";
		if(camposTabla[2].contains("ongreso")) {
			 consulta = "INSERT INTO reservas(nombre, fechaEvento, tipo, asistentes, tipoCocina, numeroJornadas, habitaciones) "
					+ "VALUES ('"+camposTabla[0]+"','"+camposTabla[1] +"','"+camposTabla[2] +"',"+Integer.parseInt(camposTabla[3])+",'"+camposTabla[4] +"',"+Integer.parseInt(camposTabla[5]) +",'"+camposTabla[6]+"')";
		} else {
			consulta = "INSERT INTO reservas(nombre, fechaEvento, tipo, asistentes, tipoCocina, tipoMesa, comensalesMesa) "
					+ "VALUES ('"+camposTabla[0]+"','"+camposTabla[1] +"','"+camposTabla[2] +"',"+Integer.parseInt(camposTabla[3])+",'"+camposTabla[4] +"','"+camposTabla[5] +"',"+Integer.parseInt(camposTabla[6])+")";
		}
		
		MainSQL.ejecutarComando(consulta);;
	}
}