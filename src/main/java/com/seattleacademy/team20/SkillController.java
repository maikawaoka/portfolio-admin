import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SkillController {
	private Logger logger = LoggerFactory.getLogger(SkillController.class);
	//private (static final) Loggerの場合は・・・

	@RequestMapping(value = "/Upload",method = RequestMethod.GET)
	//【value】upload(URLになる？)を定義
					/*
					   @RequestMapping・・・
					   "http://localhost:8080/team20/skillUpload"にアクセスするとこのコントローラーが実行される
				    　【value属性】処理対象とするURLを指定。value属性が一つだけなら「value=」は省略可
					  【method属性】GETでアクセス元のリクエスト(method="get")を処理
					　(@GetMapping("/skillUPload")の方９が省略ぽいけど使えなかった)
				    */
	public String skillUpload() {
    //skillkUploadでこの作業をする (Locale locale , Model model)とは・・・
		return "skillUpload";
//skilluploadの中身を返す
//ここは関数定義してなくても、jspの前の部分だけで引っ張ってこれる
	}
}

/*
	ロガー出力(???)
	public static final Logger logger = LoggerFactory.getLogger(SkillController.class);
	Logger.info("Welcome SkillUploadf! The client")
	これは何
*/