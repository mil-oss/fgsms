/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.
 *
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.tools;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Utility;

/**
 * Exports the database AND decrypts all encryption database columns.
 *
 * If this isn't what you want, call pg_dump which comes with postgresql
 *
 * @author AO
 */
public class DatabaseExport {

    public static void main(String[] args) throws Exception {
        System.out.println("For best results, stop the FGSMS server(s)");
        String database = "fgsms_performance";
        String url = "jdbc:postgresql://localhost:5432/";
        String username = "fgsms";
        String password = "";
        String outputFile = "export";
        Options options = new Options();
        options.addOption("u", true, "username, if not specified, fgsms will be used");
        options.addOption("p", true, "password, if not specified, you will be prompted");
        options.addOption("db", true, "database to export, default is fgsms_performance but can also be fgsms_config");
        options.addOption("server", true, "the jdbc connection url to the database server, default is jdbc:postgresql://localhost:5432/");
        options.addOption("output", true, "output file name, if not specified, export_databasename.sql will be used");
        //options.addOption("type", true, "default output is SQL, but CSV can also be specified");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("server")) {
            url = cmd.getOptionValue("server");
        }
        if (cmd.hasOption("u")) {
            username = cmd.getOptionValue("u");
        }
        if (cmd.hasOption("p")) {
            password = cmd.getOptionValue("p");
        } else {
            password = getStringPw();
        }
        if (cmd.hasOption("db")) {
            database = cmd.getOptionValue("db");
        }
        if (cmd.hasOption("output")) {
            outputFile = cmd.getOptionValue("output");
        } else {
            outputFile = outputFile + "_" + database + ".sql";
        }

        Driver d = (Driver) Class.forName("org.postgresql.Driver").newInstance();
        DriverManager.registerDriver(d);
        Connection con = DriverManager.getConnection(url + database, username, password);

        PrintWriter fos = new PrintWriter(outputFile,Constants.CHARSET);

        exportPerformance(fos, con);
         System.out.println(outputFile + " can now be imported into another postgres instance using the following command");
         System.out.println("## makes 2 test databases with all the tables and indexes");
         System.out.println("> psql -U postgres -f db_test.sql");
         System.out.println("## import the data");
         System.out.println("> psql -U postgres -d (target database) -f " + outputFile);
         System.out.println("example > psql -U postgres -d fgsms_performance_test -f " + outputFile);

    }

    private static String getStringPw() {
        System.out.print("Password = ");
        return new String(System.console().readPassword());
    }

    static String[] rawDataEncryptedColumns = new String[]{
        "requestheaders",
        "responseheaders",
        "requestxml",
        "responsexml", "memo"};

    private static void exportPerformance(PrintWriter fos, Connection con) throws Exception {

        PreparedStatement prepareStatement = con.prepareStatement("SELECT table_name FROM information_schema.tables WHERE table_schema='public'");
        ResultSet executeQuery = prepareStatement.executeQuery();
        List<String> tableNames = new ArrayList<String>();
        while (executeQuery.next()) {
            String t = executeQuery.getString(1);
            System.out.println("preparing table " + t);
            tableNames.add(t);
        }

        List<Table> tables = new ArrayList<Table>();
        executeQuery.close();
        prepareStatement.close();
        for (int i = 0; i < tableNames.size(); i++) {
            prepareStatement = con.prepareStatement("SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = 'public' AND table_name = ?");
            prepareStatement.setString(1, tableNames.get(i));
            executeQuery = prepareStatement.executeQuery();
            Table t = new Table();
            t.name = tableNames.get(i);
            while (executeQuery.next()) {
                Column c = new Column();
                c.name = executeQuery.getString("column_name");
                String type = executeQuery.getString("data_type");
                if (type.equalsIgnoreCase("bytea")) {
                    c.type = JDBCType.BYTEA;
                }
                if (type.equals("integer")) {
                    c.type = JDBCType.INTEGER;
                }
                if (type.equals("bigint")) {
                    c.type = JDBCType.BIGINT; //long
                }
                if (type.equals("text")) {
                    c.type = JDBCType.VARCHAR;
                }
                if (type.equals("boolean")) {
                    c.type = JDBCType.BOOLEAN;
                }
                if (type.equals("double precision")) {
                    c.type = JDBCType.DOUBLE;
                }
                t.columns.add(c);
                if (t.name.equalsIgnoreCase("rawdata")) {
                    for (int x = 0; x < rawDataEncryptedColumns.length; x++) {
                        if (c.name.equalsIgnoreCase(rawDataEncryptedColumns[x])) {
                            c.isFgsmsEncrypted = true;
                            break;
                        }
                    }
                }

            }
            executeQuery.close();
            prepareStatement.close();
            System.out.println("table schema scanned and prepared");
            tables.add(t);

        }

        for (int i = 0; i < tables.size(); i++) {
            System.out.println("exporting table " + tables.get(i).name);
            exportTable(tables.get(i), con, fos);
        }
        fos.flush();
        fos.close();
       

    }

    private static void exportTable(Table table, Connection con, PrintWriter fos) throws Exception {
        fos.println();
        fos.println();
        fos.println();
        PreparedStatement prepareStatement = con.prepareStatement("select * from " + table.name + " ");

        StringBuilder sb = new StringBuilder("insert into " + table.name + "(");
        for (int i = 0; i < table.columns.size(); i++) {
            sb.append(table.columns.get(i).name).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") VALUES (");
        final String header = sb.toString();
        prepareStatement.close();
        
        ResultSet executeQuery = prepareStatement.executeQuery();
        while (executeQuery.next()) {
            fos.write(header);
            for (int i = 0; i < table.columns.size(); i++) {

                switch (table.columns.get(i).type) {
                    case BIGINT: {
                        long val = executeQuery.getLong(table.columns.get(i).name);
                        fos.write(val + "");
                    }
                    break;
                    case INTEGER: {
                        int vali = executeQuery.getInt(table.columns.get(i).name);
                        fos.write(vali + "");
                    }
                    break;
                    case BOOLEAN: {
                        boolean valb = executeQuery.getBoolean(table.columns.get(i).name);
                        fos.write(valb + "");
                    }
                    break;
                    case VARCHAR: {
                        String vals = executeQuery.getString(table.columns.get(i).name);
                        fos.write("'" + escape(vals) + "'");
                    }
                    break;
                    case DOUBLE: {
                        double vals = executeQuery.getDouble(table.columns.get(i).name);
                        fos.write(vals + "");
                    }
                    break;
                    case BYTEA: {
                        if (table.columns.get(i).isFgsmsEncrypted) {
                            byte[] bits = executeQuery.getBytes(table.columns.get(i).name);
                            if (bits != null) {
                                String clear = Utility.DE(new String(bits));
                                bits = clear.getBytes(Constants.CHARSET);
                                fos.write("E'\\x" + bytesToHex(bits) + "'");
                            } else {
                                fos.write("NULL");
                            }

                        } else {
                            //dunno?
                            String vals = new String(executeQuery.getBytes(table.columns.get(i).name));
                            fos.write("E'\\x" + bytesToHex(vals.getBytes()) + "'");
                        }
                    }
                    break;
                    default:
                        throw new Exception("unhandled case for " + table.name + "," + table.columns.get(i).name);
                }
                if (i + 1 != table.columns.size()) {
                    fos.write(",");
                }
            }

            fos.write(");\n");
        }
        executeQuery.close();
        prepareStatement.close();

    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * linked from here http://stackoverflow.com/questions/26603175/how-to-dump-byte-array-to-insert-into-sql-script-java-postgresql
     * @param bytes
     * @return 
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String escape(String vals) {
        if (vals == null) {
            return "";
        }
        if (vals.endsWith("\\")) //windows!~
        {
            vals = vals + "\\";
        }
        return vals.replace("'", "\\'");
    }
}
