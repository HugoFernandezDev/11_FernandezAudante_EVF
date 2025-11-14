package pe.cert.incidentes.view;

import pe.cert.incidentes.model.Incidencia;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class IncidenciaForm extends JDialog {

    private JTextField txtTipo = new JTextField();
    private JTextField txtAula = new JTextField();
    private JTextField txtFecha = new JTextField(); // yyyy-MM-dd
    private JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Pendiente", "Procesando", "Resuelto"});
    private JTextArea taDescripcion = new JTextArea(4, 20);

    private JButton btnGuardar = new JButton("Guardar");
    private JButton btnCancelar = new JButton("Cancelar");

    private Incidencia incidencia;
    private boolean guardado = false;

    public IncidenciaForm(Frame owner) {
        super(owner, true);
        initUI();
    }

    private void initUI() {

        setTitle("Formulario de Incidencia");
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        p.add(new JLabel("Tipo *"), c);
        c.gridx = 1;
        p.add(txtTipo, c);

        c.gridx = 0; c.gridy = 1;
        p.add(new JLabel("Aula *"), c);
        c.gridx = 1;
        p.add(txtAula, c);

        c.gridx = 0; c.gridy = 2;
        p.add(new JLabel("Fecha (yyyy-MM-dd) *"), c);
        c.gridx = 1;
        p.add(txtFecha, c);

        c.gridx = 0; c.gridy = 3;
        p.add(new JLabel("Estado *"), c);
        c.gridx = 1;
        p.add(cbEstado, c);

        c.gridx = 0; c.gridy = 4;
        p.add(new JLabel("Descripción"), c);
        c.gridx = 1;
        JScrollPane sp = new JScrollPane(taDescripcion);
        p.add(sp, c);

        JPanel botones = new JPanel();
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        c.gridx = 0; c.gridy = 5; c.gridwidth = 2;
        p.add(botones, c);

        add(p);
        pack();
        setLocationRelativeTo(null);

        // Eventos
        btnCancelar.addActionListener(e -> {
            guardado = false;
            dispose();
        });

        btnGuardar.addActionListener(e -> onGuardar());
    }

    private void onGuardar() {

        String tipo = txtTipo.getText().trim();
        String aula = txtAula.getText().trim();
        String fechaStr = txtFecha.getText().trim();
        String estado = (String) cbEstado.getSelectedItem();
        String desc = taDescripcion.getText().trim();

        if (tipo.isEmpty() || aula.isEmpty() || fechaStr.isEmpty() ||
                estado == null || estado.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Complete los campos obligatorios (*)",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Fecha inválida. Use formato yyyy-MM-dd",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        incidencia = new Incidencia(tipo, aula, fecha, estado, desc);
        guardado = true;
        dispose();
    }

    public void cargarParaEdicion(Incidencia inc) {
        this.incidencia = inc;

        txtTipo.setText(inc.getTipo());
        txtAula.setText(inc.getAula());
        txtFecha.setText(inc.getFecha().toString());
        cbEstado.setSelectedItem(inc.getEstado());
        taDescripcion.setText(inc.getDescripcion());
    }

    public Incidencia getIncidencia() {
        return incidencia;
    }

    public boolean isGuardado() {
        return guardado;
    }
}
