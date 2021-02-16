package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;

import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */


public class Main {

	// static data member
	public static CountDownLatch counter = new CountDownLatch(4);


	public static void main(String[] args) {
		Gson g = new Gson();
		Input input = null;
		try {
			Reader reader = new FileReader(args[0]);
			input = g.fromJson(reader, Input.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Ewoks ewoks = Ewoks.getInstance();
		ewoks.init(input.getEwoks());
		long start = System.currentTimeMillis();
		Thread HanSoloT = new Thread(new HanSoloMicroservice());
		Thread C3POT = new Thread(new C3POMicroservice());
		Thread R2D2T = new Thread(new R2D2Microservice(input.getR2D2()));
		Thread LandoT = new Thread(new LandoMicroservice(input.getLando()));
		Thread LeiaT = new Thread(new LeiaMicroservice(input.getAttacks()));
		HanSoloT.start();
		C3POT.start();
		R2D2T.start();
		LandoT.start();
		try {
			counter.await();
		} catch (Exception e) {
		}
		LeiaT.start();
		try {
			HanSoloT.join();
			C3POT.join();
			R2D2T.join();
			LandoT.join();
			LeiaT.join();
		} catch (InterruptedException ex) {
		}
		try {

			FileWriter writer = new FileWriter(args[1]);
			g.toJson(Diary.getInstance(), writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
		}
	}
}
