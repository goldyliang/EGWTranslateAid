package bible;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestBible {

	@Test
	public void test() {
		
		BibleCN bible = BibleCN.getBibleFromFile(true);
		
		assertNotNull (bible);
		
		Verse verse = bible.getBook("Gen").getVerse(1, 1);
		assertNotNull (verse);
		
		System.out.println (verse.toFullString());
		
		String [][] toTestSuccess = {
				{" John 1:1–3, 14", "“太初有道，道与神同在，道就是神。这道太初与神同在。万物是借着他造的。凡被造的，没有一样不是借着他造的。 ... 道成了肉身住在我们中间，充充满满的有恩典有真理。我们也见过他的荣光，正是父独生子的荣光。”（约1:1-3,14）" },
				{"Matthew 1 : 21  ","“她将要生一个儿子。你要给他起名叫耶稣。因他要将自己的百姓从罪恶里救出来。”（太1:21）"},
				{"1 John 3: 4", "“凡犯罪的，就是违背律法。违背律法就是罪。”（约一3:4）"},
				{"  Matthew 2:1, 2","“当希律王的时候，耶稣生在犹太的伯利恒。有几个博士从东方来到耶路撒冷，说，那生下来作犹太人之王的在哪里？我们在东方看见他的星，特来拜他。”（太2:1,2）"},
				{"John 5: 5, 8, 9","“在那里有一个人，病了三十八年。 ... 耶稣对他说，起来，拿你的褥子走吧。那人立刻痊愈，就拿起褥子来走了。”（约5:5,8,9）"} };
		
		for (String[] v: toTestSuccess) {
		
			String txt = bible.getVerseRangeText(v[0]);
			assertNotNull (txt);

			System.out.println (txt);

			assertEquals(v[1], txt);
		}
		
	}

}
