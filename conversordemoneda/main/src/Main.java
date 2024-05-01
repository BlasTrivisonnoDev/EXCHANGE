import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    private static final String API_KEY = "eb3edf2c7be18f0aed644dac";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public static void main(String[] args) {
        String[] currencyCodes = {"ARS", "BOB", "BRL", "CLP", "COP", "USD"};
        double[] exchangeRates = getExchangeRates("USD", currencyCodes);

        if (exchangeRates != null) {
            CurrencyConverter converter = new CurrencyConverter(exchangeRates);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Seleccione la moneda de origen (USD, ARS, BOB, BRL, CLP, COP): ");
                String fromCurrency = scanner.nextLine().toUpperCase();

                if (!isValidCurrency(fromCurrency, currencyCodes)) {
                    System.out.println("Moneda no válida. Por favor, ingrese una moneda válida.");
                    continue;
                }

                System.out.println("Ingrese la cantidad a convertir: ");
                double amount = scanner.nextDouble();

                System.out.println("Seleccione la moneda de destino (USD, ARS, BOB, BRL, CLP, COP): ");
                String toCurrency = scanner.next().toUpperCase();

                if (!isValidCurrency(toCurrency, currencyCodes)) {
                    System.out.println("Moneda no válida. Por favor, ingrese una moneda válida.");
                    continue;
                }

                int fromIndex = getIndex(fromCurrency, currencyCodes);
                int toIndex = getIndex(toCurrency, currencyCodes);

                double convertedAmount = converter.convert(amount, fromIndex, toIndex);
                System.out.printf("%.2f %s = %.2f %s\n", amount, fromCurrency, convertedAmount, toCurrency);

                System.out.println("¿Desea realizar otra conversión? (s/n)");
                String choice = scanner.next();

                if (choice.equalsIgnoreCase("n")) {
                    break;
                }
            }
        } else {
            System.out.println("No se pudo obtener las tasas de cambio. Por favor, intente nuevamente más tarde.");
        }
    }

    private static double[] getExchangeRates(String baseCurrency, String[] currencyCodes) {
        try {
            URL url = new URL(BASE_URL + API_KEY + "/latest/" + baseCurrency);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                reader.close();

                String jsonResponse = response.toString();
                // Aquí puedes analizar la respuesta JSON y obtener las tasas de cambio
                // Por simplicidad, omitiremos esto aquí y devolveremos tasas de cambio ficticias
                return new double[]{1.0, 0.01, 0.18, 0.00014, 0.00028, 1.0}; // Tasas de cambio ficticias para prueba
            } else {
                System.out.println("Error al obtener la respuesta HTTP: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isValidCurrency(String currency, String[] currencyCodes) {
        for (String code : currencyCodes) {
            if (code.equalsIgnoreCase(currency)) {
                return true;
            }
        }
        return false;
    }

    private static int getIndex(String currency, String[] currencyCodes) {
        for (int i = 0; i < currencyCodes.length; i++) {
            if (currencyCodes[i].equalsIgnoreCase(currency)) {
                return i;
            }
        }
        return -1;
    }
}

class CurrencyConverter {
    private double[] exchangeRates;

    public CurrencyConverter(double[] exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    public double convert(double amount, int fromCurrencyIndex, int toCurrencyIndex) {
        double fromRate = exchangeRates[fromCurrencyIndex];
        double toRate = exchangeRates[toCurrencyIndex];
        return (amount / fromRate) * toRate;
    }
}
