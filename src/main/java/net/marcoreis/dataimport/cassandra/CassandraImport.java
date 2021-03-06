package net.marcoreis.dataimport.cassandra;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class CassandraImport {
    private static Logger logger = Logger.getLogger(CassandraImport.class);
    private Session session;
    private Cluster cluster;
    private String contactPoints = "kenobi";
    private int port = 9042;

    public static void main1(String[] args) {
	String filename = "";
	new CassandraImport().inserir(filename);
    }

    public void inserir(String filename) {
	logger.info("Vai começar");
	String cql = "INSERT INTO  %s.%s (ID, UF," + "CODIGO_MUNICIPIO, NOME_MUNICIPIO,"
		+ "NOME_BENEFICIARIO, VALOR_PAGO," + "MES_ANO)" + "VALUES (?, ?, ?, ?, ?, ?, ?)";
	cluster = Cluster.builder().addContactPoints(contactPoints).withPort(port).build();
	session = cluster.connect();

	try (BufferedReader reader = new BufferedReader(
		new InputStreamReader(new FileInputStream(filename), Charset.forName("ISO-8859-1")))) {

	    // IGNORA O HEADER
	    reader.readLine();
	    String line;
	    long ini = System.currentTimeMillis();
	    PreparedStatement pstmt = session.prepare(String.format(cql, "scalability", "bfs"));
	    while ((line = reader.readLine()) != null) {
		List<String> colunas = Arrays.asList(line.split("\t"));
		// colunas.get(0), colunas.get(1), colunas.get(2),
		// colunas.get(8), colunas.get(10), colunas.get(11));
		// BoundStatement bstmt = pstmt.bind(UUIDs.timeBased(),
		// registro.getUf(), registro.getCodigoMunicipio(),
		// registro.getNomeMunicipio(), registro.getNomeBeneficiario(),
		// Float.parseFloat(registro.getValorPago().replace(",", "")),
		// registro.getMesAno());
		// session.execute(bstmt);
	    }

	    long end = System.currentTimeMillis() - ini;
	    System.out.println("Tempo de carga: " + (end / (60 * 1000)) + " min");

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }
}
