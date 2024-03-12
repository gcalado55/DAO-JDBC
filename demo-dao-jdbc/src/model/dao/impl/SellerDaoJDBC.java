package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {

    private Connection conn;

    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {

    }

    @Override
    public void update(Seller obj) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*, department.Name as DepName " +
                            "FROM seller INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "WHERE seller.id = ?");

            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                Department dep = instantiateDepartment(rs);
                Seller sellerObj = instantiateSeller(rs, dep);
                return sellerObj;
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller sellerObj = new Seller();
        sellerObj.setId(rs.getInt("Id"));
        sellerObj.setName(rs.getString("Name"));
        sellerObj.setEmail(rs.getString("Email"));
        sellerObj.setBaseSalary(rs.getDouble("BaseSalary"));
        sellerObj.setBirthDate(rs.getDate("BirthDate"));
        sellerObj.setDepartment(dep);
        return sellerObj;
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));
        return dep;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName " +
                            "FROM seller INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "ORDER BY Name");

            rs = st.executeQuery();
            List<Seller> sellers = new ArrayList<>();
            Map<Integer, Department> sellerMap = new HashMap<>();

            while (rs.next()) {

                Department dep = sellerMap.get(rs.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    sellerMap.put(rs.getInt("DepartmentId"), dep);
                }

                Seller sellerObj = instantiateSeller(rs, dep);
                sellers.add(sellerObj);
            }
            return sellers;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName " +
                            "FROM seller INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "WHERE DepartmentId = ? " +
                            "ORDER BY Name");

            st.setInt(1, department.getId());
            rs = st.executeQuery();
            List<Seller> sellers = new ArrayList<>();
            Map<Integer, Department> sellerMap = new HashMap<>();

            while (rs.next()) {

                Department dep = sellerMap.get(rs.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    sellerMap.put(rs.getInt("DepartmentId"), dep);
                }

                Seller sellerObj = instantiateSeller(rs, dep);
                sellers.add(sellerObj);
            }
            return sellers;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }
}
