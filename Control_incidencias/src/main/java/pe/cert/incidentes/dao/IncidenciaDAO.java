package pe.cert.incidentes.dao;

import pe.cert.incidentes.model.Incidencia;
import pe.cert.incidentes.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaDAO {

    // ------------------- CREAR -------------------
    public void crear(Incidencia inc) throws SQLException {
        String sql = "INSERT INTO incidencia(tipo, aula, fecha, estado, descripcion) VALUES (?,?,?,?,?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, inc.getTipo());
            ps.setString(2, inc.getAula());
            ps.setDate(3, Date.valueOf(inc.getFecha()));
            ps.setString(4, inc.getEstado());
            ps.setString(5, inc.getDescripcion());

            ps.executeUpdate();
        }
    }

    // ------------------- ACTUALIZAR -------------------
    public void actualizar(Incidencia inc) throws SQLException {
        String sql = "UPDATE incidencia SET tipo=?, aula=?, fecha=?, estado=?, descripcion=? WHERE id=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, inc.getTipo());
            ps.setString(2, inc.getAula());
            ps.setDate(3, Date.valueOf(inc.getFecha()));
            ps.setString(4, inc.getEstado());
            ps.setString(5, inc.getDescripcion());
            ps.setInt(6, inc.getId());

            ps.executeUpdate();
        }
    }

    // ------------------- ELIMINAR -------------------
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM incidencia WHERE id=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ------------------- OBTENER POR ID -------------------
    public Incidencia obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM incidencia WHERE id=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // ------------------- LISTAR TODOS -------------------
    public List<Incidencia> listarTodos() throws SQLException {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM incidencia ORDER BY fecha DESC, id DESC";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }

        return lista;
    }

    // ------------------- BUSCAR -------------------
    public List<Incidencia> buscar(String tipo, String aula, String estado) throws SQLException {
        List<Incidencia> lista = new ArrayList<>();

        StringBuilder sb = new StringBuilder(
                "SELECT * FROM incidencia WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (tipo != null && !tipo.isBlank()) {
            sb.append(" AND tipo LIKE ?");
            params.add("%" + tipo + "%");
        }

        if (aula != null && !aula.isBlank()) {
            sb.append(" AND aula LIKE ?");
            params.add("%" + aula + "%");
        }

        if (estado != null && !estado.isBlank()) {
            sb.append(" AND estado = ?");
            params.add(estado);
        }

        sb.append(" ORDER BY fecha DESC, id DESC");

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }

        return lista;
    }

    // ------------------- MAPEAR RESULTADOS -------------------
    private Incidencia mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String tipo = rs.getString("tipo");
        String aula = rs.getString("aula");
        LocalDate fecha = rs.getDate("fecha").toLocalDate();
        String estado = rs.getString("estado");
        String descripcion = rs.getString("descripcion");

        return new Incidencia(id, tipo, aula, fecha, estado, descripcion);
    }
}
