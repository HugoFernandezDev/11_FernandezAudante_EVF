package pe.cert.incidentes.controller;

import pe.cert.incidentes.dao.IncidenciaDAO;
import pe.cert.incidentes.model.Incidencia;
import pe.cert.incidentes.view.IncidenciaForm;
import pe.cert.incidentes.view.MainFrame;

import javax.swing.*;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

// Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class IncidenciaController {

    private final IncidenciaDAO dao = new IncidenciaDAO();
    private final MainFrame main = new MainFrame(this);

    public void iniciar() {
        SwingUtilities.invokeLater(() -> {
            main.setVisible(true);
            cargarTodos();
        });
    }

    public void cargarTodos() {
        try {
            List<Incidencia> lista = dao.listarTodos();
            main.setDatosTabla(lista);
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    public void buscar(String tipo, String aula, String estado) {
        try {
            List<Incidencia> lista = dao.buscar(tipo, aula, estado);
            main.setDatosTabla(lista);
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    public void nuevoRegistro() {
        IncidenciaForm form = new IncidenciaForm(main);
        form.setVisible(true);

        if (form.isGuardado()) {
            try {
                dao.crear(form.getIncidencia());
                cargarTodos();
            } catch (SQLException ex) {
                mostrarError(ex);
            }
        }
    }

    public void editarRegistro(Incidencia inc) {
        IncidenciaForm form = new IncidenciaForm(main);
        form.cargarParaEdicion(inc);
        form.setVisible(true);

        if (form.isGuardado()) {
            Incidencia nueva = form.getIncidencia();
            nueva.setId(inc.getId());
            try {
                dao.actualizar(nueva);
                cargarTodos();
            } catch (SQLException ex) {
                mostrarError(ex);
            }
        }
    }

    public void eliminarRegistro(int id) {
        try {
            dao.eliminar(id);
            cargarTodos();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    // ----------------------------------------------------
    // REPORTE PENDIENTES POR AULA EN PANTALLA
    // ----------------------------------------------------
    public void mostrarReportePendientesPorAula() {
        try {
            List<Incidencia> todos = dao.listarTodos();
            Map<String, Long> agr = todos.stream()
                    .filter(i -> "Pendiente".equalsIgnoreCase(i.getEstado()))
                    .collect(Collectors.groupingBy(Incidencia::getAula, Collectors.counting()));

            StringBuilder sb = new StringBuilder("REPORTE - Pendientes por Aula\n\n");
            agr.forEach((aula, cnt) -> sb.append(aula).append(" : ").append(cnt).append("\n"));

            JTextArea ta = new JTextArea(sb.toString());
            ta.setEditable(false);

            JOptionPane.showMessageDialog(main, new JScrollPane(ta), "Reporte", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    // ----------------------------------------------------
    // EXPORTAR CSV
    // ----------------------------------------------------
    public void exportarCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(main) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(fc.getSelectedFile()))) {

                List<Incidencia> lista = dao.listarTodos();
                pw.println("id,tipo,aula,fecha,estado,descripcion");

                for (Incidencia i : lista) {
                    pw.println(
                            i.getId() + "," +
                                    safe(i.getTipo()) + "," +
                                    safe(i.getAula()) + "," +
                                    i.getFecha() + "," +
                                    safe(i.getEstado()) + "," +
                                    safe(i.getDescripcion())
                    );
                }

                JOptionPane.showMessageDialog(main, "CSV exportado correctamente");

            } catch (Exception ex) {
                mostrarError(ex);
            }
        }
    }

    // ----------------------------------------------------
    // EXPORTAR A PDF
    // ----------------------------------------------------
    public void exportarPDF() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("reporte_incidencias.pdf"));

        if (fc.showSaveDialog(main) == JFileChooser.APPROVE_OPTION) {
            try {
                List<Incidencia> lista = dao.listarTodos();

                Document doc = new Document();
                PdfWriter.getInstance(doc, new FileOutputStream(fc.getSelectedFile()));

                doc.open();
                doc.add(new Paragraph("REPORTE DE INCIDENCIAS\n\n"));

                PdfPTable table = new PdfPTable(6);
                table.addCell("ID");
                table.addCell("Tipo");
                table.addCell("Aula");
                table.addCell("Fecha");
                table.addCell("Estado");
                table.addCell("Descripción");

                for (Incidencia inc : lista) {
                    table.addCell(String.valueOf(inc.getId()));
                    table.addCell(inc.getTipo());
                    table.addCell(inc.getAula());
                    table.addCell(inc.getFecha().toString());
                    table.addCell(inc.getEstado());
                    table.addCell(inc.getDescripcion());
                }

                doc.add(table);
                doc.close();

                JOptionPane.showMessageDialog(main, "PDF exportado correctamente");

            } catch (Exception ex) {
                mostrarError(ex);
            }
        }
    }

    // ----------------------------------------------------
    // EXPORTAR A EXCEL (XLSX)
    // ----------------------------------------------------
    public void exportarExcel() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("reporte_incidencias.xlsx"));

        if (fc.showSaveDialog(main) == JFileChooser.APPROVE_OPTION) {
            try {
                List<Incidencia> lista = dao.listarTodos();

                Workbook wb = new XSSFWorkbook();
                Sheet sheet = wb.createSheet("Incidencias");

                // Cabecera
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("ID");
                header.createCell(1).setCellValue("Tipo");
                header.createCell(2).setCellValue("Aula");
                header.createCell(3).setCellValue("Fecha");
                header.createCell(4).setCellValue("Estado");
                header.createCell(5).setCellValue("Descripción");

                int rowNum = 1;

                for (Incidencia inc : lista) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(inc.getId());
                    row.createCell(1).setCellValue(inc.getTipo());
                    row.createCell(2).setCellValue(inc.getAula());
                    row.createCell(3).setCellValue(inc.getFecha().toString());
                    row.createCell(4).setCellValue(inc.getEstado());
                    row.createCell(5).setCellValue(inc.getDescripcion());
                }

                // Autoajustar columnas
                for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);

                FileOutputStream out = new FileOutputStream(fc.getSelectedFile());
                wb.write(out);
                out.close();
                wb.close();

                JOptionPane.showMessageDialog(main, "Excel exportado correctamente");

            } catch (Exception ex) {
                mostrarError(ex);
            }
        }
    }

    // ----------------------------------------------------
    private String safe(String s) {
        return (s == null) ? "" : s.replace("\n", " ").replace(",", " ");
    }

    private void mostrarError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(main,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
