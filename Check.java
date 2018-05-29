package check;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Check {

    public static void main(String... args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Введите количество элементов");
		int n;
		try {
			n = Integer.parseInt(br.readLine());
			Double mas[] = new Double[n];
			for (int i = 1; i <= n; i++) {
				System.out.println("Введите " + i + "-й элемент: ");
				mas[i-1] = Double.parseDouble(br.readLine());
			}
			double sum = 0;
			for (int i = 0; i < n; i++) {
				sum += mas[i];
			}
			double x = sum/n;
			System.out.println(" ans = " + x);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}