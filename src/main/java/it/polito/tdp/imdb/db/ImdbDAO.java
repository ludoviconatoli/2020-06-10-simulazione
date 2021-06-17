package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public void listAllActors(Map<Integer, Actor> idMap, String genere){
		String sql = "SELECT DISTINCT a.id, a.first_name, a.last_name, a.gender "
				+ "FROM actors a, movies_genres mg, roles r "
				+ "WHERE a.id = r.actor_id AND mg.movie_id = r.movie_id AND mg.genre = ?";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getInt("a.id"))) {
					
					Actor actor = new Actor(res.getInt("a.id"), res.getString("a.first_name"), res.getString("a.last_name"),
							res.getString("a.gender"));
					
					idMap.put(actor.getId(), actor);
				}
			}
			conn.close();
			
		} catch (SQLException e) {
			System.out.println("Errore query listAllActors");
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getGeneriFilm(){
		String sql="SELECT DISTINCT m.genre "
				+ "FROM movies_genres m";
		
		List<String> result = new LinkedList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				result.add(res.getString("m.genre"));
			}
			
			Collections.sort(result);
			conn.close();
			return result;
			
		} catch (SQLException e) {
			System.out.println("Errore in getGeneriFilm");
			return null;
		}
	}
	
	public List<Adiacenza> getArchi(Map<Integer, Actor> mappa, String genere){
		String sql ="SELECT distinct r1.actor_id, r2.actor_id, COUNT(*) AS peso "
				+ "FROM movies_genres mg, roles r1, roles r2 "
				+ "WHERE r1.movie_id = r2.movie_id AND mg.movie_id = r1.movie_id AND "
				+ "	mg.genre = ? AND r1.actor_id > r2.actor_id "
				+ "GROUP BY r1.actor_id, r2.actor_id";
		
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(mappa.containsKey(res.getInt("r1.actor_id")) && mappa.containsKey(res.getInt("r2.actor_id"))) {
					Actor a1 = mappa.get(res.getInt("r1.actor_id"));
					Actor a2 = mappa.get(res.getInt("r2.actor_id"));
					
					result.add(new Adiacenza(a1, a2, res.getInt("peso")));
				}
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			System.out.println("Errore in getArchi");
			return null;
		}
	}
	
	
}
