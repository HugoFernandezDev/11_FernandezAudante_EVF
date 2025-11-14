package pe.cert.incidentes.view;

import pe.cert.incidentes.controller.IncidenciaController;
import pe.cert.incidentes.model.Incidencia;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private IncidenciaController controller;

    private IncidenciaTableModel tableModel = new IncidenciaTableModel();
    private JTable table = new JTable(tableModel);

    private JButton btnNuevo = new JButton("Nuevo");
    private JButton btnEditar = new JButton("Editar");
    private JButton btnEliminar = new JButton("Eliminar");
    private JButton btnRefrescar = new JButton("Refrescar");

    private JTextField txtBuscarTipo = new JTextField(10);
    private JTextField txtBuscarAula = new JTextField(10);
    private JComboBox<String> cbBuscarEstado =
            new JComboBox<>(new String[]{"", "Pendiente", "Procesando", "Resuelto"});

    private JButton btnBuscar = new JButton("Buscar");
    private JButton btnReporte = new JButton("Pendientes por Aula");
    private JButton btnExportCSV = new JButton("Exportar CSV");
    private JButton btnExportPDF = new JButton("Exportar PDF");
    private JButton btnExportExcel = new JButton("Exportar Excel");

    public MainFrame(IncidenciaController controller) {
        this.controller = controller;
        initUI();
        attachEvents();
    }

    private void initUI() {
        setTitle("Sistema de Incidencias Técnicas");
        setSize(1000, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // PANEL DE BUSQUEDA (TOP)
        JPanel top = new JPanel();
        top.add(new JLabel("Tipo:"));
        top.add(txtBuscarTipo);
        top.add(new JLabel("Aula:"));
        top.add(txtBuscarAula);
        top.add(new JLabel("Estado:"));
        top.add(cbBuscarEstado);
        top.add(btnBuscar);
        top.add(btnRefrescar);

        // PANEL DE BOTONES (BOTTOM)
        JPanel botones = new JPanel();
        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnReporte);
        botones.add(btnExportCSV);
        botones.add(btnExportPDF);
        botones.add(btnExportExcel);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void attachEvents() {

        btnNuevo.addActionListener(e -> controller.nuevoRegistro());

        btnEditar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione una fila",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            controller.editarRegistro(tableModel.getRow(row));
        });

        btnEliminar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione una fila",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Eliminar incidencia seleccionada?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                controller.eliminarRegistro(tableModel.getRow(row).getId());
            }
        });

        btnRefrescar.addActionListener(e -> controller.cargarTodos());

        btnBuscar.addActionListener(e ->
                controller.buscar(
                        txtBuscarTipo.getText().trim(),
                        txtBuscarAula.getText().trim(),
                        (String) cbBuscarEstado.getSelectedItem()
                )
        );

        btnReporte.addActionListener(e -> controller.mostrarReportePendientesPorAula());

        btnExportCSV.addActionListener(e -> controller.exportarCSV());
        btnExportPDF.addActionListener(e -> controller.exportarPDF());
        btnExportExcel.addActionListener(e -> controller.exportarExcel());
    }

    public void setDatosTabla(java.util.List<Incidencia> lista) {
        tableModel.setDatos(lista);
    }
}
