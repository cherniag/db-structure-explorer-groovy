package mobi.nowtechnologies.server.service.itunes;

import org.junit.*;
import static org.junit.Assert.*;
/**
 * Author: Gennadii Cherniaiev Date: 4/24/2015
 */
public class AppStoreReceiptParserTest {

    AppStoreReceiptParser appStoreReceiptParser = new AppStoreReceiptParser();

    @Test
    public void getProductId() throws Exception {
        String productId = appStoreReceiptParser.getProductId(validReceipt);
        assertEquals("com.musicqubed.o2.subscription", productId);
    }

    @Test(expected = RuntimeException.class)
    public void getProductIdFail() throws Exception {
        String notValidReceipt = "123";
        appStoreReceiptParser.getProductId(notValidReceipt);
    }

    private static String validReceipt = "ewoJInNpZ25hdHVyZSIgPSAiQWxPTUVHWGNzNjkzLzBPbTdLYVBLeXY3N2hlU0R5WHdpYlZBcmhjQnluRE16NWFFcHJWU3cwQUZjcHZPTnYyWjhTUHlxWXpuOTdGUTFnL25HQ2gzV0JycXp2ZUFlRlFsOG" +
                                         "paUEtUNk1tdTY1TUYxeDVxSzdzK2FtNWRSV0JuOEhqaytpbWxGOEtoLzJmZ3ZjN1NPUmpmak5FNVNLdWo1U3V1S0YvNzl3b1hUdEFBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NCdXA0K1BBaG0vTE1BMEdD" +
                                         "U3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMG" +
                                         "VURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEUwTURZd056QXdNREl5TVZvWERURTJNRFV4T0RFNE16RXpNRm93W" +
                                         "kRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpM" +
                                         "akVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNbVRFdUxnamltTHdSSnh5MW9FZjBlc1VORFZFSWU2d0Rzbm5hbDE0aE5CdDF2MTk1WDZuOTNZTzdnaTN" +
                                         "vclBTdXg5RDU1NFNrTXArU2F5Zzg0bFRjMzYyVXRtWUxwV25iMzRucXlHeDlLQlZUeTVPR1Y0bGpFMU93QytvVG5STStRTFJDbWVOeE1iUFpoUzQ3VCtlWnRERWhWQjl1c2szK0pNMkNvZ2Z3bzdBZ01CQU" +
                                         "FHamNqQndNQjBHQTFVZERnUVdCQlNKYUVlTnVxOURmNlpmTjY4RmUrSTJ1MjJzc0RBTUJnTlZIUk1CQWY4RUFqQUFNQjhHQTFVZEl3UVlNQmFBRkRZZDZPS2RndElCR0xVeWF3N1hRd3VSV0VNNk1BNEdBM" +
                                         "VVkRHdFQi93UUVBd0lIZ0RBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQWVhSlYyVTUxcnhmY3FBQWU1QzIvZkVXOEtVbDRpTzRsTXV0YTdONlh6UDFwWkl6MU5r" +
                                         "a0N0SUl3ZXlOajVVUllISytIalJLU1U5UkxndU5sMG5rZnhxT2JpTWNrd1J1ZEtTcTY5Tkluclp5Q0Q2NlI0Szc3bmI5bE1UQUJTU1lsc0t0OG9OdGxoZ1IvMWtqU1NSUWNIa3RzRGNTaVFHS01ka1NscDR" +
                                         "BeVhmN3ZuSFBCZTR5Q3dZVjJQcFNOMDRrYm9pSjNwQmx4c0d3Vi9abEwyNk0ydWVZSEtZQ3VYaGRxRnd4VmdtNTJoM29lSk9PdC92WTRFY1FxN2VxSG02bTAzWjliN1BSellNMktHWEhEbU9Nazd2RHBlTV" +
                                         "ZsTERQU0dZejErVTNzRHhKemViU3BiYUptVDdpbXpVS2ZnZ0VZN3h4ZjRjemZIMHlqNXdOelNHVE92UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSa" +
                                         "GRHVXRjSE4wSWlBOUlDSXlNREV6TFRBMkxURXhJREEzT2pJek9qVXhJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluQjFjbU5vWVhObExXUmhkR1V0YlhNaUlEMGdJakUwTWpZd09ETTRNekF3" +
                                         "TURBaU93b0pJblZ1YVhGMVpTMXBaR1Z1ZEdsbWFXVnlJaUE5SUNJMk5EaGxPV05oT1dNM01UQmxZVFZqT1dKak9UY3lObVV3T0Raa05qbGpPR1U1WkRVNU4yWmhJanNLQ1NKdmNtbG5hVzVoYkMxMGNtRnV" +
                                         "jMkZqZEdsdmJpMXBaQ0lnUFNBaU1qZ3dNREF3TURJeE1UZzVOamszSWpzS0NTSmxlSEJwY21WekxXUmhkR1VpSUQwZ0lqRTBNamczTmpJeU16QXdNREFpT3dvSkltRndjQzFwZEdWdExXbGtJaUE5SUNJMU" +
                                         "9UVTBNak01TWpZaU93b0pJblJ5WVc1ellXTjBhVzl1TFdsa0lpQTlJQ0l5T0RBd01EQXdOemd6TXpVNU1UY2lPd29KSW5GMVlXNTBhWFI1SWlBOUlDSXhJanNLQ1NKdmNtbG5hVzVoYkMxd2RYSmphR0Z6W" +
                                         "lMxa1lYUmxMVzF6SWlBOUlDSXhNemN3T1RZd05qTXhNREF3SWpzS0NTSjJaWEp6YVc5dUxXVjRkR1Z5Ym1Gc0xXbGtaVzUwYVdacFpYSWlJRDBnSWpFME16WTFORFF4SWpzS0NTSjFibWx4ZFdVdGRtVnVa" +
                                         "Rzl5TFdsa1pXNTBhV1pwWlhJaUlEMGdJakU1UVRZMk9FSkdMVFkwTXpBdE5FWTBSUzFCUXpjeExURTNSVEkxTXpkRk1ERXpOU0k3Q2draWQyVmlMVzl5WkdWeUxXeHBibVV0YVhSbGJTMXBaQ0lnUFNBaU1" +
                                         "qZ3dNREF3TURBMU1qTXhNelF6SWpzS0NTSnBkR1Z0TFdsa0lpQTlJQ0kyTURrd09EVTJOemNpT3dvSkltVjRjR2x5WlhNdFpHRjBaUzFtYjNKdFlYUjBaV1FpSUQwZ0lqSXdNVFV0TURRdE1URWdNVFE2TW" +
                                         "pNNk5UQWdSWFJqTDBkTlZDSTdDZ2tpY0hWeVkyaGhjMlV0WkdGMFpTSWdQU0FpTWpBeE5TMHdNeTB4TVNBeE5Eb3lNem8xTUNCRmRHTXZSMDFVSWpzS0NTSndjbTlrZFdOMExXbGtJaUE5SUNKamIyMHViW" +
                                         "FZ6YVdOeGRXSmxaQzV2TWk1emRXSnpZM0pwY0hScGIyNGlPd29KSW1WNGNHbHlaWE10WkdGMFpTMW1iM0p0WVhSMFpXUXRjSE4wSWlBOUlDSXlNREUxTFRBMExURXhJREEzT2pJek9qVXdJRUZ0WlhKcFky" +
                                         "RXZURzl6WDBGdVoyVnNaWE1pT3dvSkltOXlhV2RwYm1Gc0xYQjFjbU5vWVhObExXUmhkR1VpSUQwZ0lqSXdNVE10TURZdE1URWdNVFE2TWpNNk5URWdSWFJqTDBkTlZDSTdDZ2tpY0hWeVkyaGhjMlV0Wkd" +
                                         "GMFpTMXdjM1FpSUQwZ0lqSXdNVFV0TURNdE1URWdNRGM2TWpNNk5UQWdRVzFsY21sallTOU1iM05mUVc1blpXeGxjeUk3Q2draVltbGtJaUE5SUNKamIyMHViWFZ6YVdOeGRXSmxaQzV2TWlJN0Nna2lZbl" +
                                         "p5Y3lJZ1BTQWlNUzR4SWpzS2ZRPT0iOwoJInBvZCIgPSAiMjgiOwoJInNpZ25pbmctc3RhdHVzIiA9ICIwIjsKfQ\\u003d\\u003d";
}