package MyLb.BackEnd.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/technical-analysis")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
@EnableAsync
@Component
public class TechnicalAnalysisController {

    private final RestTemplate restTemplate;
    private final String GEMINI_API_KEY = "AIzaSyAN3WPReDT6KHp26_0B73su7xuvcBSHYzg";
    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private final String STOCK_API_BASE_URL = "http://localhost:8000";

    // Cache optimisé pour les analyses techniques
    private final Map<String, Map<String, Object>> analysisCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutes

    // Cache pour les données des stocks (moins fréquemment mis à jour)
    private final Map<String, Object> stocksCache = new ConcurrentHashMap<>();
    private long stocksCacheTimestamp = 0;
    private static final long STOCKS_CACHE_DURATION = 2 * 60 * 1000; // 2 minutes

    @Autowired
    public TechnicalAnalysisController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Endpoint principal pour l'analyse technique de tous les stocks
     */
    @GetMapping("/all-stocks")
    public ResponseEntity<?> getAllStocksTechnicalAnalysis() {
        long startTime = System.currentTimeMillis();

        try {
            System.out.println("📊 [TechnicalAnalysis] Analyse technique demandée pour tous les stocks");

            // Vérifier le cache principal
            String cacheKey = "all-stocks-analysis";
            if (isCacheValid(cacheKey)) {
                Map<String, Object> cachedAnalysis = analysisCache.get(cacheKey);
                System.out.println("💨 [Cache] Analyse technique servie depuis le cache");

                Map<String, Object> response = new HashMap<>(cachedAnalysis);
                response.put("cached", true);
                response.put("cacheAge", System.currentTimeMillis() - cacheTimestamps.get(cacheKey));
                response.put("responseTime", System.currentTimeMillis() - startTime);

                return ResponseEntity.ok(response);
            }

            // Récupérer tous les stocks depuis l'API FastAPI (avec cache)
            List<Map<String, Object>> allStocks = fetchAllStocksWithCache();

            if (allStocks.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                        "Aucun stock disponible", startTime
                ));
            }

            System.out.println("🔄 Début de l'analyse technique pour " + allStocks.size() + " stocks");

            // Analyser les stocks en parallèle pour optimiser les performances
            List<CompletableFuture<Map<String, Object>>> futures = allStocks.stream()
                    .map(stock -> analyzeStockAsync(stock))
                    .collect(Collectors.toList());

            // Attendre que toutes les analyses soient terminées
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0])
            );

            // Récupérer les résultats
            List<Map<String, Object>> stocksAnalysis = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(analysis -> analysis != null && analysis.get("success").equals(true))
                    .collect(Collectors.toList());

            // Préparer la réponse globale
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalStocks", allStocks.size());
            response.put("analyzedStocks", stocksAnalysis.size());
            response.put("stocksAnalysis", stocksAnalysis);
            response.put("analysisTimestamp", System.currentTimeMillis());
            response.put("responseTime", System.currentTimeMillis() - startTime);
            response.put("message", "Analyse technique complète générée pour " + stocksAnalysis.size() + " stocks");

            // Mettre en cache
            updateCache(cacheKey, response);

            System.out.println("✅ [TechnicalAnalysis] Analyse technique terminée pour " +
                    stocksAnalysis.size() + "/" + allStocks.size() + " stocks en " +
                    (System.currentTimeMillis() - startTime) + "ms");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ [TechnicalAnalysis] Erreur: " + e.getMessage());
            return createServerErrorResponse(e, startTime);
        }
    }

    /**
     * Analyse asynchrone d'un stock individuel
     */
    @Async
    public CompletableFuture<Map<String, Object>> analyzeStockAsync(Map<String, Object> stock) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Integer stockId = (Integer) stock.get("id_stock");
                String stockName = (String) stock.get("nom_stock");

                System.out.println("🔍 Analyse du stock: " + stockName + " (ID: " + stockId + ")");

                return analyzeSingleStock(stockId, stock);
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de l'analyse asynchrone: " + e.getMessage());
                return createFailedStockAnalysis(stock, "Erreur lors de l'analyse: " + e.getMessage());
            }
        });
    }

    /**
     * Analyse un stock individuel
     */
    private Map<String, Object> analyzeSingleStock(Integer stockId, Map<String, Object> stockInfo) {
        Map<String, Object> stockAnalysis = new HashMap<>();
        stockAnalysis.put("stockId", stockId);
        stockAnalysis.put("stockInfo", stockInfo);

        try {
            // Récupérer l'historique des prix avec gestion d'erreur
            List<Map<String, Object>> priceHistory = fetchStockHistoryWithRetry(stockId, 2);

            if (priceHistory == null || priceHistory.isEmpty()) {
                return createFailedStockAnalysis(stockInfo, "Aucun historique de prix disponible");
            }

            // Calculer les indicateurs techniques
            Map<String, Object> technicalIndicators = calculateTechnicalIndicators(priceHistory);

            // Préparer l'analyse de base
            stockAnalysis.put("success", true);
            stockAnalysis.put("priceHistory", priceHistory.subList(0, Math.min(priceHistory.size(), 10))); // Garder seulement les 10 derniers prix
            stockAnalysis.put("technicalIndicators", technicalIndicators);
            stockAnalysis.put("totalDataPoints", priceHistory.size());
            stockAnalysis.put("analysisStatus", "complete");

            // Analyser avec Gemini AI (avec timeout)
            try {
                Map<String, Object> aiAnalysis = analyzeWithGeminiAI(priceHistory, technicalIndicators, stockInfo);
                stockAnalysis.put("aiAnalysis", aiAnalysis);
                stockAnalysis.put("analysisStatus", "complete_with_ai");
            } catch (Exception e) {
                System.err.println("⚠️ Analyse AI échouée pour le stock " + stockId + ": " + e.getMessage());
                stockAnalysis.put("aiAnalysis", createBasicAnalysis(technicalIndicators));
                stockAnalysis.put("analysisStatus", "basic");
            }

            return stockAnalysis;

        } catch (Exception e) {
            System.err.println("❌ Erreur analyse stock " + stockId + ": " + e.getMessage());
            return createFailedStockAnalysis(stockInfo, "Erreur technique: " + e.getMessage());
        }
    }

    /**
     * Récupère tous les stocks avec cache
     */
    private List<Map<String, Object>> fetchAllStocksWithCache() {
        long currentTime = System.currentTimeMillis();

        // Vérifier le cache des stocks
        if ((currentTime - stocksCacheTimestamp) < STOCKS_CACHE_DURATION && stocksCache.containsKey("stocks")) {
            System.out.println("💨 [StocksCache] Stocks récupérés depuis le cache");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cachedStocks = (List<Map<String, Object>>) stocksCache.get("stocks");
            return cachedStocks;
        }

        try {
            String url = STOCK_API_BASE_URL + "/stocks";
            System.out.println("🌐 Récupération des stocks depuis: " + url);

            Map<String, Object> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, Map.class
            ).getBody();

            if (response != null && response.containsKey("stocks")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> stocks = (List<Map<String, Object>>) response.get("stocks");

                // Mettre en cache
                stocksCache.put("stocks", stocks);
                stocksCacheTimestamp = currentTime;

                System.out.println("📋 Stocks récupérés: " + stocks.size() + " stocks");
                return stocks;
            }

            return new ArrayList<>();

        } catch (Exception e) {
            System.err.println("❌ Erreur récupération des stocks: " + e.getMessage());

            // Retourner le cache expiré en cas d'erreur
            if (stocksCache.containsKey("stocks")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> cachedStocks = (List<Map<String, Object>>) stocksCache.get("stocks");
                System.out.println("🔄 Utilisation du cache expiré en raison d'erreur");
                return cachedStocks;
            }

            return new ArrayList<>();
        }
    }

    /**
     * Récupère l'historique des prix avec mécanisme de retry
     */
    private List<Map<String, Object>> fetchStockHistoryWithRetry(Integer stockId, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String url = STOCK_API_BASE_URL + "/stock-history/" + stockId +
                        "?limit=100&order_by=date_creation&order_direction=desc";

                Map<String, Object> response = restTemplate.exchange(
                        url, HttpMethod.GET, null, Map.class
                ).getBody();

                if (response != null && response.containsKey("history")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> history = (List<Map<String, Object>>) response.get("history");

                    if (!history.isEmpty()) {
                        System.out.println("📈 Historique récupéré pour stock " + stockId + ": " + history.size() + " points");
                        return history;
                    }
                }

                // Si pas d'historique, pas besoin de retry
                return new ArrayList<>();

            } catch (Exception e) {
                System.err.println("❌ Tentative " + attempt + "/" + maxRetries + " échouée pour stock " + stockId + ": " + e.getMessage());

                if (attempt == maxRetries) {
                    return new ArrayList<>();
                }

                // Attendre avant de retry
                try {
                    Thread.sleep(1000 * attempt); // Backoff exponentiel
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return new ArrayList<>();
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * Analyse avec Gemini AI avec timeout implicite
     */
    private Map<String, Object> analyzeWithGeminiAI(List<Map<String, Object>> priceHistory,
                                                    Map<String, Object> indicators,
                                                    Map<String, Object> stockInfo) {
        long startTime = System.currentTimeMillis();

        try {
            System.out.println("🤖 [GeminiAI] Début de l'analyse technique AI");

            String analysisPrompt = buildTechnicalAnalysisPrompt(priceHistory, indicators, stockInfo);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", analysisPrompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String apiUrl = GEMINI_API_URL + "?key=" + GEMINI_API_KEY;

            Map<String, Object> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, Map.class
            ).getBody();

            String aiResponse = extractAIResponse(response);

            Map<String, Object> analysisResult = new HashMap<>();
            analysisResult.put("analysis", aiResponse);
            analysisResult.put("modelUsed", "gemini-2.5-flash");
            analysisResult.put("analysisTimestamp", System.currentTimeMillis());
            analysisResult.put("aiStatus", "success");
            analysisResult.put("aiProcessingTime", System.currentTimeMillis() - startTime);

            System.out.println("✅ [GeminiAI] Analyse technique AI terminée en " +
                    (System.currentTimeMillis() - startTime) + "ms");

            return analysisResult;

        } catch (Exception e) {
            System.err.println("❌ [GeminiAI] Erreur analyse technique: " + e.getMessage());
            throw new RuntimeException("Erreur API Gemini: " + e.getMessage());
        }
    }

    /**
     * Construction du prompt pour l'analyse technique
     */
    private String buildTechnicalAnalysisPrompt(List<Map<String, Object>> priceHistory,
                                                Map<String, Object> indicators,
                                                Map<String, Object> stockInfo) {
        StringBuilder prompt = new StringBuilder();

        String stockName = (String) stockInfo.getOrDefault("nom_stock", "Stock Inconnu");
        String stockSymbol = (String) stockInfo.getOrDefault("symbol_stock", "N/A");

        prompt.append("ANALYSE TECHNIQUE PROFESSIONNELLE - RECOMMANDATIONS DE TRADING\n\n");
        prompt.append("CONTEXTE: Tu es un analyste technique expert avec 15 ans d'expérience en trading.\n\n");
        prompt.append("ACTION: ").append(stockName).append(" (").append(stockSymbol).append(")\n");
        prompt.append("DONNÉES TECHNIQUES:\n");

        @SuppressWarnings("unchecked")
        Map<String, Object> movingAverages = (Map<String, Object>) indicators.get("movingAverages");
        @SuppressWarnings("unchecked")
        Map<String, Object> rsi = (Map<String, Object>) indicators.get("rsi");
        @SuppressWarnings("unchecked")
        Map<String, Object> macd = (Map<String, Object>) indicators.get("macd");
        @SuppressWarnings("unchecked")
        Map<String, Object> bollinger = (Map<String, Object>) indicators.get("bollingerBands");
        @SuppressWarnings("unchecked")
        Map<String, Object> supportResistance = (Map<String, Object>) indicators.get("supportResistance");
        @SuppressWarnings("unchecked")
        Map<String, Object> trends = (Map<String, Object>) indicators.get("trends");

        prompt.append("📈 PRIX COURANT: ").append(indicators.get("currentPrice")).append(" DT\n");
        prompt.append("📊 RSI: ").append(String.format("%.2f", rsi.get("value"))).append(" (").append(rsi.get("level")).append(") - Signal: ").append(rsi.get("signal")).append("\n");
        prompt.append("🔍 MACD: ").append(String.format("%.4f", macd.get("macdLine"))).append(" | Signal: ").append(macd.get("signal")).append("\n");
        prompt.append("📏 MOVING AVERAGES:\n");
        prompt.append("   - SMA20: ").append(String.format("%.2f", movingAverages.get("sma20"))).append(" (Écart: ").append(String.format("%.2f", movingAverages.get("priceVsSma20"))).append("%)\n");
        prompt.append("   - SMA50: ").append(String.format("%.2f", movingAverages.get("sma50"))).append(" (Écart: ").append(String.format("%.2f", movingAverages.get("priceVsSma50"))).append("%)\n");
        prompt.append("🎯 BOLLINGER BANDS: Position ").append(bollinger.get("position")).append(" | Largeur: ").append(String.format("%.2f", bollinger.get("width"))).append("%\n");
        prompt.append("📊 SUPPORT/RESISTANCE:\n");
        prompt.append("   - Support: ").append(String.format("%.2f", supportResistance.get("support"))).append(" DT (Distance: ").append(String.format("%.2f", supportResistance.get("distanceToSupport"))).append("%)\n");
        prompt.append("   - Résistance: ").append(String.format("%.2f", supportResistance.get("resistance"))).append(" DT (Distance: ").append(String.format("%.2f", supportResistance.get("distanceToResistance"))).append("%)\n");
        prompt.append("📈 TENDANCES: Court=").append(trends.get("shortTerm")).append(" | Moyen=").append(trends.get("mediumTerm")).append(" | Long=").append(trends.get("longTerm")).append("\n");
        prompt.append("📊 VOLATILITÉ: ").append(String.format("%.2f", ((Map<?, ?>) indicators.get("volatility")).get("value"))).append("% (").append(((Map<?, ?>) indicators.get("volatility")).get("level")).append(")\n");
        prompt.append("⚡ SIGNAL COMPOSITE: ").append(indicators.get("tradingSignal")).append(" | Confiance: ").append(indicators.get("confidence")).append("%\n");
        prompt.append("🎲 NIVEAU DE RISQUE: ").append(indicators.get("riskLevel")).append("\n\n");

        prompt.append("DERNIERS PRIX (échantillon):\n");
        priceHistory.stream().limit(5).forEach(price -> {
            String date = (String) price.get("date_creation");
            prompt.append("   - ").append(date.substring(0, 16)).append(": ").append(price.get("prix")).append(" DT\n");
        });

        prompt.append("\n🎯 TON ANALYSE DOIT INCLURE (sois précis et technique):\n");
        prompt.append("1. SIGNAL DE TRADING PRÉCIS (ACHAT FORT/ACHAT FAIBLE/NEUTRE/VENTE FAIBLE/VENTE FORTE)\n");
        prompt.append("2. PRIX CIBLE (TARGET PRICE) avec justification technique\n");
        prompt.append("3. STOP LOSS recommandé basé sur les supports\n");
        prompt.append("4. HORIZON TEMPOREL optimal (day trading/swing trading/investissement)\n");
        prompt.append("5. CONFIANCE du signal (1-5 étoiles) ★★★★★\n");
        prompt.append("6. RISQUES PRINCIPAUX identifiés\n");
        prompt.append("7. STRATÉGIE DE SORTIE recommandée\n\n");

        prompt.append("FORMAT: Sois direct, technique, avec des chiffres précis. Utilise le format de trading professionnel.");

        return prompt.toString();
    }

    // ============================================================================
    // MÉTHODES DE CALCUL DES INDICATEURS TECHNIQUES
    // ============================================================================

    private Map<String, Object> calculateTechnicalIndicators(List<Map<String, Object>> priceHistory) {
        Map<String, Object> indicators = new HashMap<>();

        if (priceHistory.isEmpty()) return indicators;

        // Extraire les prix
        List<Double> prices = priceHistory.stream()
                .map(entry -> (Double) entry.get("prix"))
                .collect(Collectors.toList());

        // Prix actuels et historiques
        double currentPrice = prices.get(0);
        double price5DaysAgo = prices.size() > 5 ? prices.get(5) : prices.get(prices.size() - 1);
        double price30DaysAgo = prices.size() > 30 ? prices.get(30) : prices.get(prices.size() - 1);

        // 1. MOVING AVERAGES
        double sma20 = calculateSMA(prices, 20);
        double sma50 = calculateSMA(prices, 50);
        double ema12 = calculateEMA(prices, 12);
        double ema26 = calculateEMA(prices, 26);

        // 2. RSI (Relative Strength Index)
        double rsi = calculateRSI(prices, 14);

        // 3. MACD
        double macdLine = ema12 - ema26;
        double signalLine = calculateEMA(Arrays.asList(macdLine, macdLine, macdLine, macdLine, macdLine,
                macdLine, macdLine, macdLine, macdLine), 9);
        double macdHistogram = macdLine - signalLine;

        // 4. Bollinger Bands
        double[] bollinger = calculateBollingerBands(prices, 20);
        double upperBand = bollinger[0];
        double lowerBand = bollinger[1];
        double middleBand = bollinger[2];

        // 5. Support and Resistance
        double[] supportResistance = calculateSupportResistance(prices);
        double supportLevel = supportResistance[0];
        double resistanceLevel = supportResistance[1];

        // 6. Price Trends
        String shortTermTrend = determineTrend(prices, 5);
        String mediumTermTrend = determineTrend(prices, 20);
        String longTermTrend = determineTrend(prices, 50);

        // 7. Volatility
        double volatility = calculateVolatility(prices, 20);

        // Populer les indicateurs
        indicators.put("currentPrice", currentPrice);
        indicators.put("priceChange5D", ((currentPrice - price5DaysAgo) / price5DaysAgo) * 100);
        indicators.put("priceChange30D", ((currentPrice - price30DaysAgo) / price30DaysAgo) * 100);

        indicators.put("movingAverages", Map.of(
                "sma20", sma20,
                "sma50", sma50,
                "ema12", ema12,
                "ema26", ema26,
                "priceVsSma20", ((currentPrice - sma20) / sma20) * 100,
                "priceVsSma50", ((currentPrice - sma50) / sma50) * 100
        ));

        indicators.put("rsi", Map.of(
                "value", rsi,
                "level", getRSILevel(rsi),
                "signal", getRSISignal(rsi)
        ));

        indicators.put("macd", Map.of(
                "macdLine", macdLine,
                "signalLine", signalLine,
                "histogram", macdHistogram,
                "signal", getMACDSignal(macdLine, signalLine)
        ));

        indicators.put("bollingerBands", Map.of(
                "upper", upperBand,
                "middle", middleBand,
                "lower", lowerBand,
                "position", getBollingerPosition(currentPrice, upperBand, lowerBand),
                "width", ((upperBand - lowerBand) / middleBand) * 100
        ));

        indicators.put("supportResistance", Map.of(
                "support", supportLevel,
                "resistance", resistanceLevel,
                "distanceToSupport", ((currentPrice - supportLevel) / currentPrice) * 100,
                "distanceToResistance", ((resistanceLevel - currentPrice) / currentPrice) * 100
        ));

        indicators.put("trends", Map.of(
                "shortTerm", shortTermTrend,
                "mediumTerm", mediumTermTrend,
                "longTerm", longTermTrend
        ));

        indicators.put("volatility", Map.of(
                "value", volatility,
                "level", getVolatilityLevel(volatility)
        ));

        // Signal d'achat/vente composite
        indicators.put("tradingSignal", generateTradingSignal(indicators));
        indicators.put("confidence", calculateConfidence(indicators));
        indicators.put("riskLevel", calculateRiskLevel(indicators));

        return indicators;
    }

    private double calculateSMA(List<Double> prices, int period) {
        if (prices.size() < period) return prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        return prices.subList(0, period).stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private double calculateEMA(List<Double> prices, int period) {
        if (prices.size() < period) return calculateSMA(prices, prices.size());

        double multiplier = 2.0 / (period + 1);
        double ema = calculateSMA(prices.subList(0, period), period);

        for (int i = period; i < prices.size(); i++) {
            ema = (prices.get(i) - ema) * multiplier + ema;
        }
        return ema;
    }

    private double calculateRSI(List<Double> prices, int period) {
        if (prices.size() <= period) return 50.0;

        List<Double> gains = new ArrayList<>();
        List<Double> losses = new ArrayList<>();

        for (int i = 1; i < period; i++) {
            double difference = prices.get(i-1) - prices.get(i);
            if (difference > 0) {
                gains.add(difference);
                losses.add(0.0);
            } else {
                gains.add(0.0);
                losses.add(Math.abs(difference));
            }
        }

        double avgGain = gains.stream().mapToDouble(Double::doubleValue).average().orElse(0.001);
        double avgLoss = losses.stream().mapToDouble(Double::doubleValue).average().orElse(0.001);

        double rs = avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }

    private double[] calculateBollingerBands(List<Double> prices, int period) {
        if (prices.size() < period) {
            double avg = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            return new double[]{avg, avg, avg};
        }

        List<Double> sublist = prices.subList(0, period);
        double sma = sublist.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double stdDev = Math.sqrt(sublist.stream()
                .mapToDouble(price -> Math.pow(price - sma, 2))
                .average().orElse(0));

        return new double[]{
                sma + (2 * stdDev), // Upper band
                sma,                // Middle band
                sma - (2 * stdDev)  // Lower band
        };
    }

    private double[] calculateSupportResistance(List<Double> prices) {
        double min = prices.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = prices.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double range = max - min;

        return new double[]{
                min + (range * 0.15), // Support level (15% above min)
                max - (range * 0.15)  // Resistance level (15% below max)
        };
    }

    private String determineTrend(List<Double> prices, int period) {
        if (prices.size() < period) return "NEUTRE";

        double first = prices.get(period - 1);
        double last = prices.get(0);
        double change = ((last - first) / first) * 100;

        if (change > 2) return "HAUSSIER";
        if (change < -2) return "BAISSIER";
        return "NEUTRE";
    }

    private double calculateVolatility(List<Double> prices, int period) {
        if (prices.size() < period) return 0;

        List<Double> sublist = prices.subList(0, period);
        double mean = sublist.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = sublist.stream()
                .mapToDouble(price -> Math.pow(price - mean, 2))
                .average().orElse(0);

        return Math.sqrt(variance) / mean * 100; // Volatility as percentage
    }

    // ============================================================================
    // MÉTHODES D'INTERPRÉTATION DES SIGNALS
    // ============================================================================

    private String getRSILevel(double rsi) {
        if (rsi > 70) return "SURACHAT";
        if (rsi < 30) return "SURVENTE";
        return "NEUTRE";
    }

    private String getRSISignal(double rsi) {
        if (rsi > 70) return "VENTE";
        if (rsi < 30) return "ACHAT";
        return "NEUTRE";
    }

    private String getMACDSignal(double macdLine, double signalLine) {
        if (macdLine > signalLine) return "ACHAT";
        if (macdLine < signalLine) return "VENTE";
        return "NEUTRE";
    }

    private String getBollingerPosition(double price, double upper, double lower) {
        if (price > upper) return "AU-DESSUS";
        if (price < lower) return "EN-DESSOUS";
        return "DANS LA BANDE";
    }

    private String getVolatilityLevel(double volatility) {
        if (volatility > 3) return "ÉLEVÉE";
        if (volatility > 1.5) return "MODÉRÉE";
        return "FAIBLE";
    }

    private String generateTradingSignal(Map<String, Object> indicators) {
        @SuppressWarnings("unchecked")
        Map<String, Object> rsi = (Map<String, Object>) indicators.get("rsi");
        @SuppressWarnings("unchecked")
        Map<String, Object> macd = (Map<String, Object>) indicators.get("macd");
        @SuppressWarnings("unchecked")
        Map<String, Object> trends = (Map<String, Object>) indicators.get("trends");

        int buySignals = 0;
        int sellSignals = 0;

        // RSI
        if ("ACHAT".equals(rsi.get("signal"))) buySignals++;
        if ("VENTE".equals(rsi.get("signal"))) sellSignals++;

        // MACD
        if ("ACHAT".equals(macd.get("signal"))) buySignals++;
        if ("VENTE".equals(macd.get("signal"))) sellSignals++;

        // Trends
        if ("HAUSSIER".equals(trends.get("shortTerm"))) buySignals++;
        if ("BAISSIER".equals(trends.get("shortTerm"))) sellSignals++;

        if (buySignals >= 2 && buySignals > sellSignals) return "ACHAT FORT";
        if (sellSignals >= 2 && sellSignals > buySignals) return "VENTE FORTE";
        if (buySignals > sellSignals) return "ACHAT FAIBLE";
        if (sellSignals > buySignals) return "VENTE FAIBLE";
        return "NEUTRE";
    }

    private double calculateConfidence(Map<String, Object> indicators) {
        @SuppressWarnings("unchecked")
        Map<String, Object> rsi = (Map<String, Object>) indicators.get("rsi");
        double rsiValue = (double) rsi.get("value");

        // Plus le RSI est extrême, plus la confiance est élevée
        double rsiConfidence = Math.abs(50 - rsiValue) / 50 * 100;

        return Math.min(rsiConfidence, 85); // Max 85% de confiance
    }

    private String calculateRiskLevel(Map<String, Object> indicators) {
        @SuppressWarnings("unchecked")
        Map<String, Object> volatility = (Map<String, Object>) indicators.get("volatility");
        String volatilityLevel = (String) volatility.get("level");

        return switch (volatilityLevel) {
            case "ÉLEVÉE" -> "ÉLEVÉ";
            case "MODÉRÉE" -> "MOYEN";
            default -> "FAIBLE";
        };
    }

    // ============================================================================
    // MÉTHODES UTILITAIRES
    // ============================================================================

    private boolean isCacheValid(String cacheKey) {
        return analysisCache.containsKey(cacheKey) &&
                (System.currentTimeMillis() - cacheTimestamps.get(cacheKey)) < CACHE_DURATION;
    }

    private void updateCache(String cacheKey, Map<String, Object> analysis) {
        analysisCache.put(cacheKey, analysis);
        cacheTimestamps.put(cacheKey, System.currentTimeMillis());
    }

    private Map<String, Object> createBasicAnalysis(Map<String, Object> indicators) {
        Map<String, Object> basicAnalysis = new HashMap<>();
        basicAnalysis.put("analysis", "Analyse technique de base - Consultez les indicateurs techniques pour les détails.");
        basicAnalysis.put("modelUsed", "technical-indicators");
        basicAnalysis.put("analysisTimestamp", System.currentTimeMillis());
        basicAnalysis.put("aiStatus", "basic");
        return basicAnalysis;
    }

    private Map<String, Object> createFailedStockAnalysis(Map<String, Object> stockInfo, String message) {
        Map<String, Object> failedAnalysis = new HashMap<>();
        failedAnalysis.put("stockId", stockInfo.get("id_stock"));
        failedAnalysis.put("stockInfo", stockInfo);
        failedAnalysis.put("success", false);
        failedAnalysis.put("message", message);
        failedAnalysis.put("analysisStatus", "failed");
        return failedAnalysis;
    }

    private Map<String, Object> createErrorResponse(String message, long startTime) {
        return Map.of(
                "success", false,
                "message", message,
                "responseTime", System.currentTimeMillis() - startTime
        );
    }

    private ResponseEntity<?> createServerErrorResponse(Exception e, long startTime) {
        return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Erreur lors de l'analyse technique: " + e.getMessage(),
                "responseTime", System.currentTimeMillis() - startTime
        ));
    }

    private String extractAIResponse(Map<String, Object> geminiResponse) {
        try {
            if (geminiResponse == null) return "Réponse vide de l'API Gemini";
            if (geminiResponse.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) geminiResponse.get("error");
                return "Erreur Gemini: " + error.get("message");
            }
            if (geminiResponse.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) geminiResponse.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    if (candidate.containsKey("content")) {
                        Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                        if (content.containsKey("parts")) {
                            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                            if (parts != null && !parts.isEmpty()) {
                                Object text = parts.get(0).get("text");
                                return text != null ? text.toString() : "Réponse texte vide";
                            }
                        }
                    }
                }
            }
            return "Structure de réponse inattendue";
        } catch (Exception e) {
            return "Erreur extraction: " + e.getMessage();
        }
    }
}