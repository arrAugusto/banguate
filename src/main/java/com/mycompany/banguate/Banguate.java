/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.banguate;

/**
 *
 * @author agr12
 */
public class Banguate {

    public static void main(String[] args) {
        try {
            tipoCambio tc = new tipoCambio();
            
            // JSON completo con fecha, referencia, compra y venta
            String resultado = tc.getTipoCambioDia();
            System.out.println(resultado);
            
            // Solo el valor numérico de referencia
            double referencia = tc.getReferencia();
            System.out.println("Q" + referencia);
        } catch (Exception ex) {
            System.getLogger(Banguate.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
