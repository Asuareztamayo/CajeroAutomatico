public class Main {
  static Conexion conexion;
  
  public static void main(String[] args) {
    conexion = new Conexion();
    conexion.conectar();
    System.out.println("Bienvenido al banco H&B");
    System.out.println("*********************");
    System.out.println("Introduzca su tarjeta");
    conexion.acceso();
  }
}