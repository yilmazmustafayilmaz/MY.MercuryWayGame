package com.mogap.mercuryway;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.omg.CORBA.PERSIST_STORE;

import java.util.Vector;

public class MercuryWay extends ApplicationAdapter {
	private SpriteBatch oyunEkranı;
	private OrthographicCamera haraketliKamera;
	private Texture arkaPlan;
	private Animation mekik;
	private Vector2 mekikPozisyon;
	private float gecenSure = 0;
	private Texture mekikFrame1, mekikFrame2, mekikFrame3;
	private static final float MEKIK_BASLANGIC_X_KONUM = 50;
	private static final float MEKIK_BASLANGIC_Y_KONUM = 240;
	private enum OyunDurumu{Start, Running, GameOver}
	private OyunDurumu oyunDurumu = OyunDurumu.Start;
	private Vector2 yercekimi = new Vector2();
	private Vector2 mekikYerçekimi = new  Vector2();
	private static final float MEKİK_ZIPLAMA = 350;
	private static final float YERCEKIMI = -20;
	private static final float MEKİK_HIZ_X = 200;
	private TextureRegion zeminBölüm, tavanBölüm;
	private float ilkZeminPosizyonX;
	private TextureRegion engel, engelTers;
	private Array<Engel> engeller = new Array<Engel>();
	private TextureRegion start, gameOver;
	private Sound carpısma;
	private Rectangle mekikBoyut = new Rectangle();
	private Rectangle engelBoyut = new Rectangle();
	private OrthographicCamera arayuzKamera;
//	private ShapeRenderer shapeRenderer;
	private BitmapFont font;
	private Music fonMuzik;
	private int puan = 0;

	public MercuryWay() {
	}


	@Override
	public void create () {
		oyunEkranı = new SpriteBatch();

		haraketliKamera = new OrthographicCamera();
		haraketliKamera.setToOrtho(false, 800, 480);

		arkaPlan = new Texture("arkaPlan.png");

		mekikFrame1 = new Texture("mekik1.png");
		mekikFrame2 = new Texture("mekik2.png");
		mekikFrame3 = new Texture("mekik3.png");

		mekik = new Animation(0.05f, new TextureRegion(mekikFrame1), new TextureRegion(mekikFrame2), new TextureRegion(mekikFrame3));
		mekik.setPlayMode(Animation.PlayMode.LOOP);

		mekikPozisyon = new Vector2();

		zeminBölüm = new TextureRegion(new Texture("zemin.png"));
		tavanBölüm = new TextureRegion(zeminBölüm);
		tavanBölüm.flip(true, true);

		engel = new TextureRegion(new Texture("engel.png") );
		engelTers = new TextureRegion(engel);
		engelTers.flip(true, true);

		arayuzKamera = new OrthographicCamera();
		arayuzKamera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		arayuzKamera.update();

		start = new TextureRegion(new Texture("start.png"));
		gameOver = new TextureRegion(new Texture("gameover.png"));
		carpısma = Gdx.audio.newSound(Gdx.files.internal("patlama.wav"));

 //		shapeRenderer = new ShapeRenderer();

		font = new BitmapFont(Gdx.files.internal("font.fnt"));

		fonMuzik = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		fonMuzik.setLooping(true);
		fonMuzik.play();

		merkuruResetle();
	}
	@Override
	public void render () {

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		merkuruGuncelle();
		merkuruCizdir();
	}
	private void merkuruResetle() {
		mekikPozisyon.set(MEKIK_BASLANGIC_X_KONUM, MEKIK_BASLANGIC_Y_KONUM);

		haraketliKamera.position.x = 400;

		yercekimi.set(0, YERCEKIMI);
		mekikYerçekimi.set(0, 0);

		ilkZeminPosizyonX = 0;

		engeller.clear();
		for(int i = 0; i < 5; i++){
			boolean isDown = MathUtils.randomBoolean();

			engeller.add(new Engel(700 + i * 200, isDown ? 480 - engel.getRegionHeight():0, isDown ? engelTers: engel, isDown));
		}
		puan = 0;

	}
	private void merkuruGuncelle() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		gecenSure += deltaTime;

		if (Gdx.input.justTouched()) {

			if (oyunDurumu == OyunDurumu.Start){
				oyunDurumu = OyunDurumu.Running;
			}

			if (oyunDurumu == OyunDurumu.Running){
				mekikYerçekimi.set(MEKİK_HIZ_X, MEKİK_ZIPLAMA);
			}

			if(oyunDurumu == OyunDurumu.GameOver){
				oyunDurumu = OyunDurumu.Start;
				merkuruResetle();
			}
		}
		if(oyunDurumu != OyunDurumu.Start){
			mekikYerçekimi.add(yercekimi);
		}
		mekikPozisyon.mulAdd(mekikYerçekimi, deltaTime);
		haraketliKamera.position.x = mekikPozisyon.x + 350;

		System.out.println("mekik pozisyon x;" + mekikPozisyon.x);

		if (haraketliKamera.position.x > zeminBölüm.getRegionWidth() + ilkZeminPosizyonX + 400){
			ilkZeminPosizyonX += zeminBölüm.getRegionWidth(); }

		mekikBoyut.set(mekikPozisyon.x, mekikPozisyon.y, ((TextureRegion)mekik.getKeyFrames()[0]).getRegionWidth(), ((TextureRegion)mekik.getKeyFrames()[0]).getRegionHeight());

		for(Engel engel: engeller){

			engelBoyut.set(engel.pozisyon.x + (engel.resim.getRegionWidth() - 50) / 2 + 20, engel.pozisyon.y, 10, engel.resim.getRegionHeight() - 5);
			if (haraketliKamera.position.x - engel.pozisyon.x > 400 + engel.resim.getRegionWidth()){
				boolean isDown = MathUtils.randomBoolean();
				engel.pozisyon.x += 5 * 200;
				engel.pozisyon.y = isDown ? 480 - this.engel.getRegionHeight() : 0;
				engel.resim = isDown ? engelTers : this.engel;
				engel.gecildi = false;
			}
			if(mekikBoyut.overlaps(engelBoyut)){

				if(oyunDurumu != OyunDurumu.GameOver){
					carpısma.play();
				}
				oyunDurumu = OyunDurumu.GameOver;
				mekikYerçekimi.x = 0;
			}
			if(engel.pozisyon.x < mekikPozisyon.x && !engel.gecildi){
				puan++;
				engel.gecildi = true;
			}
		}
		if(mekikPozisyon.y < zeminBölüm.getRegionHeight() - 20 ||
				mekikPozisyon.y + ((TextureRegion)mekik.getKeyFrames()[0]).getRegionHeight() >
				480 - zeminBölüm.getRegionHeight() + 20){

			if(oyunDurumu != OyunDurumu.GameOver){
				carpısma.play();
			}
			oyunDurumu = OyunDurumu.GameOver;
			mekikYerçekimi.x = 0;
		}

	}
	private void merkuruCizdir() {
		haraketliKamera.update();

		oyunEkranı.setProjectionMatrix(haraketliKamera.combined);
		oyunEkranı.begin();
		oyunEkranı.draw(arkaPlan, haraketliKamera.position.x - arkaPlan.getWidth() / 2,0);

		for(Engel engel : engeller){
			oyunEkranı.draw(engel.resim, engel.pozisyon.x, engel.pozisyon.y);
		}
		oyunEkranı.draw(zeminBölüm, ilkZeminPosizyonX, 0);
		oyunEkranı.draw(zeminBölüm, ilkZeminPosizyonX + zeminBölüm.getRegionWidth(), 0);
		oyunEkranı.draw(tavanBölüm, ilkZeminPosizyonX, 510 - tavanBölüm.getRegionHeight());
		oyunEkranı.draw(tavanBölüm, ilkZeminPosizyonX + tavanBölüm.getRegionWidth(), 510 - tavanBölüm.getRegionHeight());
		oyunEkranı.draw((TextureRegion) mekik.getKeyFrame(gecenSure), mekikPozisyon.x, mekikPozisyon.y);
		oyunEkranı.end();

		oyunEkranı.setProjectionMatrix(arayuzKamera.combined);
		oyunEkranı.begin();

		if (oyunDurumu == OyunDurumu.Start){
			oyunEkranı.draw(start, Gdx.graphics.getWidth() / 2 - start.getRegionWidth() / 2, Gdx.graphics.getHeight() / 2 - start.getRegionHeight() / 2);
		}
		if(oyunDurumu == OyunDurumu.GameOver){
			oyunEkranı.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getRegionWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getRegionHeight() / 2);
		}
		if(oyunDurumu == OyunDurumu.GameOver || oyunDurumu == OyunDurumu.Running){
			font.draw(oyunEkranı, "" + puan, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 10);
		}
		oyunEkranı.end();

/*		shapeRenderer.setProjectionMatrix(haraketliKamera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(2, 3, 2, 3);
		shapeRenderer.rect(mekikBoyut.x, mekikBoyut.y, mekikBoyut.width, mekikBoyut.height);

		for(Engel engel : engeller){
			shapeRenderer.rect(engel.pozisyon.x + (engel.resim.getRegionWidth() - 50) / 2 + 20, engel.pozisyon.y, engelBoyut.width,  engelBoyut.height);
		}
		shapeRenderer.end();*/
	}


}
