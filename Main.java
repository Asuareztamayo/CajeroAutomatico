public class Main {
  static Conexion conexion;
  // static Scanner scanner = new Scanner(System.in);
  
  public static void main(String[] args) {
    conexion = new Conexion();
    conexion.conectar();
    System.out.println("Bienvenido al banco H&B");
    System.out.println("*********************");
    System.out.println("Introduzca su tarjeta");
    conexion.acceder();
  }
}