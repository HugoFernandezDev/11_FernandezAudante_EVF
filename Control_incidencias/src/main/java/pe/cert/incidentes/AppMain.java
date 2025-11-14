package pe.cert.incidentes;

import pe.cert.incidentes.controller.IncidenciaController;

public class AppMain {
    public static void main(String[] args) {
        IncidenciaController controller = new IncidenciaController();
        controller.iniciar();
    }
}
