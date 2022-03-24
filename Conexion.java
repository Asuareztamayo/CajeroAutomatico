import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.io.*;

public class Conexion {
    public static final String url = "jdbc:mysql://localhost:3307/banco";
    public static final String user = "root";
    public static final String password = "Adminadmin2022";
    Connection cx;
    static Scanner scanner = new Scanner(System.in);
    public static Scanner td = new Scanner(System.in);

    public Connection conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cx = DriverManager.getConnection(url, user, password);
            System.out.println("Se conecto a la base de datos");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return cx;
    }

    public void desconectar() {
        try {
            cx.close();
        } catch (SQLException e) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Connection getConn() throws SQLException {
        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        return (Connection) DriverManager.getConnection(url, user, password);
    }

    public void acceder() {
        try (Connection conn = getConn();
                Statement stmt = conn.createStatement()) {
            ResultSet resultado = stmt.executeQuery("SELECT numero, pin FROM banco.tarjetadebito");
            int a = 0;
            ArrayList<Number> resultadosTD = new ArrayList<>();
            ArrayList<Integer> resultadosPinTD = new ArrayList<>();
            while (resultado.next()) {

                Number numero = (Number) resultado.getObject(1);
                int pin = (int) resultado.getObject(2);
                resultadosTD.add(numero);
                resultadosPinTD.add(pin);
                int pin2 = 0;
                for (Number number : resultadosTD) {
                    if (a == 0) {
                        System.out.println("*********************");
                        System.out.println("Introduce el pin de : " + number);
                        pin2 = td.nextInt();
                    }
                    if (a == 0 && pin2 == pin) {
                        System.out.println("*********************");
                        System.out.println("Has accedido a tu cuenta");
                        System.out.println("*********************");
                        menu();
                    }
                    a += 1;
                }
            }
            System.out.println("El pin no es correcto");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

    }

    public void menu() {

        int option = 0;

        do {
            System.out.println("¿Qué operación desea realizar?");
            System.out.println("1.Ver movimientos");
            System.out.println("2.Retirar dinero");
            System.out.println("3.Ingresar dinero");
            System.out.println("4.Hacer transferencia");
            System.out.println("5.Cambiar el pin");
            System.out.println("6.Salir");

            option = scanner.nextInt();

            switch (option) {
                case 1:
                    verMovimientos();
                    break;
                case 2:
                    retirarDinero();
                    break;
                case 3:
                    ingresarDinero();
                    break;
                case 4:
                    realizarTransferencia();
                    break;
                case 5:
                    cambiarPin();
                    break;
                case 6:
                    salir();
                    break;
            }
        } while (option != 6);
    }

    public void verMovimientos() {

        String movimientos = "";

        try (Connection conn = getConn();
                Statement stmt = conn.createStatement()) {
            ResultSet resultado = stmt.executeQuery(
                    "SELECT movimientos.fecha,  movimientos.mensaje , movimientos.tipoDeMovimiento, movimientos.cantidad FROM banco.tarjetadebito INNER JOIN banco.movimientos ON tarjetadebito.cuenta_idcuenta = movimientos.cuenta_idcuenta WHERE idtarjetaDebito = 1;");
            System.out.println("*********************");
            System.out.println("Sus movimientos son: ");
            System.out.println("*********************");
            while (resultado.next()) {

                Date fecha = resultado.getDate(1);
                String mensaje = (String) resultado.getObject(2);
                String tipoDeMovimiento = (String) resultado.getObject(3);
                Number cantidad = (Number) resultado.getObject(4);
                movimientos = (fecha + " " + mensaje + " " + tipoDeMovimiento + " " + cantidad + "E");
                System.out.println(movimientos);
            }
            System.out.println("*********************");

            System.out.println("Su saldo actual es de : " + saldo().toString() + "E");
            System.out.println("*********************");

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public Double saldo() {
        Double sum = 0.0;
        try (Connection conn = getConn();
                Statement stmt = conn.createStatement()) {
            ResultSet resultado = stmt.executeQuery(
                    "SELECT cantidad FROM banco.tarjetadebito INNER JOIN banco.movimientos ON tarjetadebito.cuenta_idcuenta = movimientos.cuenta_idcuenta WHERE idtarjetaDebito = 1;");
            while (resultado.next()) {
                BigDecimal tmp = (BigDecimal) resultado.getObject(1);
                sum = tmp.doubleValue() + sum;
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return sum;
    }

    public void retirarDinero() {
        Console cnsl = System.console();
        if (cnsl == null) {
            System.out.println("No console available");
            return;
        }
        try (Connection conn = getConn();
                Statement stmt = conn.createStatement()) {
            ResultSet resultado = stmt.executeQuery(
                    "SELECT cantidad FROM banco.tarjetadebito INNER JOIN banco.movimientos ON tarjetadebito.cuenta_idcuenta = movimientos.cuenta_idcuenta WHERE idtarjetaDebito = 1;");
            Double sum = 0.0;
            Double dineroRetiro = 0.0;
            Double rest = 0.0;
            while (resultado.next()) {
                BigDecimal tmp = (BigDecimal) resultado.getObject(1);
                sum = tmp.doubleValue() + sum;
            }
            System.out.println("*********************");
            System.out.println("Cuánto dinero desea retirar?");
            dineroRetiro = Double.parseDouble(cnsl.readLine("-"));
            if(dineroRetiro > saldo()){
                System.out.println("No tienes dinero suficiente");
                return;
            }else{
            rest = sum - dineroRetiro;
            System.out.println("*********************");
            System.out.println("Retiro  " + dineroRetiro + "E" + " su saldo actual es de : " + rest + "E");
            Statement stmt2 = conn.createStatement();
            dineroRetiro = dineroRetiro * -1;
            stmt2.executeUpdate(
                    "INSERT INTO `banco`.`movimientos` (`fecha`, `mensaje`, `tipoDeMovimiento`, `cantidad`, `cuenta_idcuenta`) VALUES ('2022-03-21 15:27:00', 'Gastos', 'Retiro', '"
                            + dineroRetiro + "', '1');");
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void ingresarDinero() {
        Console cnsl = System.console();
        if (cnsl == null) {
            System.out.println("No console available");
            return;
        }
        try (Connection conn = getConn();
                Statement stmt = conn.createStatement()) {
            ResultSet resultado = stmt.executeQuery(
                    "SELECT cantidad FROM banco.tarjetadebito INNER JOIN banco.movimientos ON tarjetadebito.cuenta_idcuenta = movimientos.cuenta_idcuenta WHERE idtarjetaDebito = 1;");
            Double sum = 0.0;
            Double ingreso = 0.0;
            Double total = 0.0;
            while (resultado.next()) {
                BigDecimal tmp = (BigDecimal) resultado.getObject(1);
                sum = tmp.doubleValue() + sum;
            }
            System.out.println("*********************");
            System.out.println("Cuánto dinero desea ingresar?");
            ingreso = Double.parseDouble(cnsl.readLine());
            total = sum + ingreso;
            System.out.println("*********************");
            System.out.println("Ingreso  " + ingreso + "E" + " su saldo actual es de : " + total + "E");
            Statement stmt2 = conn.createStatement();
            stmt2.executeUpdate(
                    "INSERT INTO `banco`.`movimientos` (`fecha`, `mensaje`, `tipoDeMovimiento`, `cantidad`, `cuenta_idcuenta`) VALUES ('2022-03-21 17:57:00', 'Devuelvo dinero', 'ingreso', '" + ingreso + "', '1');");

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        System.out.println("Haz ingresado dinero");
        System.out.println("*********************");
    }

    public ArrayList cuentas(){
        ArrayList<String> resultadoCuentas = new ArrayList<>();
        try (Connection conn = getConn();
                Statement stmt = conn.createStatement()) {
            ResultSet resultado = stmt.executeQuery("SELECT IBAN FROM banco.cuenta");
            while(resultado.next()){
                String IBAN = (String) resultado.getObject(1);
                resultadoCuentas.add(IBAN);
            }
        
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return resultadoCuentas;
    }
    
    public void realizarTransferencia() {
        Console cnsl = System.console();
        if (cnsl == null) {
            System.out.println("No console available");
            return;
        }
        Double dineroTransferencia = 0.0;
        String cuentaTransferencia=null;
        try (Connection conn = getConn();
            Statement stmt = conn.createStatement()) {
            ResultSet resultado = stmt.executeQuery(
                    "SELECT cantidad FROM banco.tarjetadebito INNER JOIN banco.movimientos ON tarjetadebito.cuenta_idcuenta = movimientos.cuenta_idcuenta WHERE idtarjetaDebito = 1;");
            Double sum = 0.0;
            Double rest = 0.0;
            while (resultado.next()) {
                BigDecimal tmp = (BigDecimal) resultado.getObject(1);
                sum = tmp.doubleValue() + sum;
            }
            System.out.println("*********************");
            System.out.println("Cuánto dinero desea transferir?");
            dineroTransferencia = Double.parseDouble(cnsl.readLine("-"));
            if(dineroTransferencia > saldo()){
                System.out.println("No tienes dinero suficiente");
                return;
            }else{
            rest = sum - dineroTransferencia;
            System.out.println("¿A quién le quiere transferir el dinero?");
            cuentaTransferencia = (String) cnsl.readLine();
            if(cuentas().contains(cuentaTransferencia)){
                System.out.println("*********************");
                System.out.println("Transfirio  " + dineroTransferencia + "E" + " su saldo actual es de : " + rest + "E");
                Statement stmt2 = conn.createStatement();
                dineroTransferencia = dineroTransferencia * -1;
                stmt2.executeUpdate(
                        "INSERT INTO `banco`.`movimientos` (`fecha`, `mensaje`, `tipoDeMovimiento`, `cantidad`, `cuenta_idcuenta`) VALUES ('2022-03-24 15:27:00', 'Gastos', 'Transferencia', '" + dineroTransferencia + "', '1');");
                }else{
                    System.out.println("La cuenta introducida no existe.");
                }
                
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        int idcuenta = 0;
        Statement stmt2 = null;
        try (Connection conn = getConn();
            Statement stmt = conn.createStatement()) {
            ResultSet resultado = stmt.executeQuery( 
                    "SELECT cantidad, cuenta_idcuenta, IBAN FROM banco.movimientos INNER JOIN banco.cuenta ON cuenta_idcuenta = idcuenta WHERE IBAN = '"+cuentaTransferencia+"';");
            while (resultado.next()) {
                idcuenta = resultado.getInt(2);
            }
            stmt2 = conn.createStatement();
            dineroTransferencia = dineroTransferencia * -1;
            stmt2.executeUpdate(
                    "INSERT INTO `banco`.`movimientos` (`fecha`, `mensaje`, `tipoDeMovimiento`, `cantidad`, `cuenta_idcuenta`) VALUES ('2022-03-24 15:27:00', 'Gastos', 'Transferencia', '" + dineroTransferencia + "', '"+ idcuenta +"');");
            System.out.println("Transferencia realizada con exito.");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void salir(){
        System.out.println("Gracias por la confianza, que tenga un feliz día");
    }

    public void cambiarPin() {
        Console cnsl = System.console();
        if (cnsl == null) {
            System.out.println("No console available");
            return;
        }
        Integer pin = 0;
        Integer nuevoPin = 0;
        try (Connection conn = getConn();
            Statement stmt = conn.createStatement()) {
            ResultSet resultado = stmt.executeQuery(
                    "SELECT pin FROM banco.tarjetadebito WHERE idtarjetaDebito = 1;");
            while (resultado.next()) {
                pin = (Integer) resultado.getObject(1);
            }
            System.out.println("*********************");
            System.out.println("Cuál desea que sea su nuevo pin?");
            nuevoPin = Integer.parseInt(cnsl.readLine());
            if(nuevoPin == pin){
                System.out.println("El pin coincide, pruebe con uno nuevo");
            }else{
                Statement stmt2 = conn.createStatement();
                stmt2.executeUpdate(
                        "UPDATE `banco`.`tarjetacredito` SET `pin` = '"+ nuevoPin +"' WHERE (`idtarjetaCredito` = '1') and (`cuenta_idcuenta` = '1');");
                    System.out.println("El pin se ha cambiado");
                }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}