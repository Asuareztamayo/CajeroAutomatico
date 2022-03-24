import java.sql.Date;

public class Movimiento {
    int idmovimientos;
    Date fecha;
    String mensaje;
    String TipoDeMovimiento;
    Double cantidad;
    int cuenta_idcuenta;


    public int getIdmovimientos() {
        return idmovimientos;
    }
    public void setIdmovimientos(int idmovimientos) {
        this.idmovimientos = idmovimientos;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public String getTipoDeMovimiento() {
        return TipoDeMovimiento;
    }
    public void setTipoDeMovimiento(String tipoDeMovimiento) {
        TipoDeMovimiento = tipoDeMovimiento;
    }
    public Double getCantidad() {
        return cantidad;
    }
    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }
    public int getCuenta_idcuenta() {
        return cuenta_idcuenta;
    }
    public void setCuenta_idcuenta(int cuenta_idcuenta) {
        this.cuenta_idcuenta = cuenta_idcuenta;
    }    
}
