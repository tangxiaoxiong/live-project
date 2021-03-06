package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.AppointmentDao;
import pojo.Appointment;
import util.DBUtil;

public class AppointService {

	static public int totalNum =10;
	static public int turn = 0;

	// 开始摇号及turn次数++
	public void beginAppoint() {
		turn++;
	}

	// 是否手机号以及身份证号已经在数据库中存在
	public boolean doesHaveAppointed(Appointment appointment) {
		String phoneString = appointment.getPhoneNumber();
		String idString = appointment.getidNumber();
		String sqlString = "select * from appointment where idNumber='"+idString +"' or phoneNumber='"+phoneString + "'";
		System.out.println(sqlString);
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sqlString);) {
			ResultSet rs = ps.executeQuery();
			if (rs == null) {
				return true;
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	public Integer getChanceNum(Appointment appointment) {
		int rowNum = 0;
		String phoneString = appointment.getPhoneNumber();
		String idString = appointment.getidNumber();
		String sqlString = "select * from appointment where " + "(idNumber='"+idString +"' or phoneNumber='"+phoneString+
				"' and status='true')";
		System.out.println(sqlString);
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sqlString);) {
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				rowNum++;
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return rowNum;

	}

	// 是否已经获取三次中签
	public boolean lessThanThreeChance(Appointment appointment) {
		if (getChanceNum(appointment) < 3) {
			return true;
		} else
			return false;
	}

	// 是否能参与此次预约,如果
	public boolean doesJoinThisAppoint(Appointment appointment) {
		if (doesHaveAppointed(appointment) && lessThanThreeChance(appointment)) {
			return true;
		} else
			return false;
	}

	// 插入数据库
	public void insertIntoDataBase(Appointment appointment) {
		if(appointment.getQuantity() < totalNum) {
			totalNum-=appointment.getQuantity();	
			appointment.setStatus(true);
		}
		else {
			appointment.setStatus(false);
		}
		
		String idString = appointment.getID();
		String nameString = appointment.getName();
		String idNumString = appointment.getidNumber();
		String phoneString = appointment.getPhoneNumber();
		Integer turnNum = appointment.getTurn();
		Integer quantity = appointment.getQuantity();
		Boolean status = appointment.getStatus();
		String sqlString = "insert into appointment values"
				+ "("+idString+","+nameString+","+idNumString+","+phoneString+","+turnNum+","+quantity+","+status+")";
		System.out.println(sqlString);
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sqlString);) {
			//ResultSet rs = ps.executeQuery(sqlString);
			AppointmentDao dao = new AppointmentDao();
			dao.insert(appointment);
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}