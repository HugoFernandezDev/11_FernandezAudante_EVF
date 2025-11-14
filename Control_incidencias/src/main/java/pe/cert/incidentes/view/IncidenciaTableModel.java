package pe.cert.incidentes.view;


import pe.cert.incidentes.model.Incidencia;


import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class IncidenciaTableModel extends AbstractTableModel {
    private final String[] cols = {"ID","Tipo","Aula","Fecha","Estado","Descripción"};
    private List<Incidencia> datos = new ArrayList<>();
    private final DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public void setDatos(List<Incidencia> lista) { this.datos = lista; fireTableDataChanged(); }
    public Incidencia getRow(int row){ return datos.get(row); }


    @Override public int getRowCount() { return datos.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int col) { return cols[col]; }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Incidencia i = datos.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> i.getId();
            case 1 -> i.getTipo();
            case 2 -> i.getAula();
            case 3 -> i.getFecha().format(f);
            case 4 -> i.getEstado();
            case 5 -> i.getDescripcion();
            default -> null;
        };
    }
}