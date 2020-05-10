package com.seattleacademy.team20;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@Controller
public class SkillController {

  private static final Logger logger = LoggerFactory.getLogger(SkillController.class);
  // private (static final) Loggerの場合は・・・

  @Autowired
  private JdbcTemplate jdbcTemplate;
  //MySQLとの接続するため
  /*
   * Simply selects the home view to render by returning its name.
   */

  @RequestMapping(value = "/Upload", method = RequestMethod.GET)
  //【value】upload(URLになる？)を定義
  /*
     @RequestMapping・・・リクエストURLに対してどのメソッドが処理を実行するか定義するアノテーション。
     "<%=request.getContextPath()%>/Upload"にアクセスするとこのコントローラーが実行される
  　【value属性】処理対象とするURLを指定。value属性が一つだけなら「value=」は省略可
    【method属性】GETでアクセス元のリクエスト(method="get")を処理
  　(@GetMapping("/skillUPload")の方９が省略ぽいけど使えなかった)
  */
  public String skillUpload(Locale locale, Model model) {

    logger.info("Welcome SkillUpload! The client locale is {}.");
    try {
      initialize();
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    };
    List<Skill> skills = selectSkills();
    uploadSkill(skills);

    return "skillUpload";
    //skilluploadの中身を返す
    //ここは関数定義してなくても、jspの前の部分だけで引っ張ってこれる
  }

  public List<Skill> selectSkills() {
    //skillsテーブルのデータを全件取得するSQL
    final String sql = "SELECT * FROM skills";
    //SQL実行
    return jdbcTemplate.query(sql, new RowMapper<Skill>() {
      public Skill mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Skill(rs.getInt("id"), rs.getString("category"), rs.getString("name"), rs.getInt("score"));
      }
    });
  }

  //【???】管理者権限
  private FirebaseApp app;

  //SDK初期化
  public void initialize() throws IOException {
    FileInputStream refreshToken = new FileInputStream(
        "/Users/ju0j8/seattle-key/develop-portfolio-b0eb0-462b448d8f91.json");
    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(refreshToken))
        .setDatabaseUrl("https://develop-portfolio-b0eb0.firebaseio.com")
        .build();

    //Oauth2.0更新トークンを利用する
    app = FirebaseApp.initializeApp(options, "other"); /*【???】←に"other"入れる理由*/
  }

  //データの保存
  public void uploadSkill(List<Skill> skills) {
	    final FirebaseDatabase database = FirebaseDatabase.getInstance(app); //app引数
	    DatabaseReference ref = database.getReference("skills");//


  //Map型のリストを作る  (MapはStringで聞かれたものに対してObjectで返すようにしている)
  /*ここでMysqlからFirebaseの型に当てはめるために、データを成形する処理をしている*/
                   //List<データ型名> オブジェクト名 = new ArrayList<データ型名>();
  List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
  Map<String, Object> map;
  Map<String, List<Skill>> skillMap = skills.stream().collect(Collectors.groupingBy(Skill::getCategory));/*ここ*/
	for (Map.Entry<String, List<Skill>> entry : skillMap.entrySet()) {
	      map = new HashMap<>();
	      map.put("categoey", entry.getKey());
	      map.put("skill", entry.getValue());
	      dataList.add(map);
	      }


  //リアルタイムデータベース更新 request→reference
    ref.setValue(dataList, new DatabaseReference.CompletionListener() {
    	@Override
    	public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
    		if (databaseError != null) {
    			System.out.println("Data could be saved" + databaseError.getMessage());
    		} else {
    			System.out.println("Data save successfuly");
    		}
    	}
    });
  }
}