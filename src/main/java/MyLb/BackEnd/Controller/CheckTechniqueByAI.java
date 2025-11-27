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
@RequestMapping("/api/ai-analysis")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
@EnableAsync
@Component
public class CheckTechniqueByAI {
    private final RestTemplate restTemplate;
    private final String GEMINI_API_KEY = "AIzaSyAN3WPReDT6KHp26_0B73su7xuvcBSHYzg";
    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private final String STOCK_API_BASE_URL = "http://localhost:8000";

    // Cache optimisé pour les analyses techniques
    private final Map<String, Map<String, Object>> analysisCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutes

    // Cache pour les données des stocks
    private final Map<String, Object> stocksCache = new ConcurrentHashMap<>();
    private long stocksCacheTimestamp = 0;
    private static final long STOCKS_CACHE_DURATION = 2 * 60 * 1000; // 2 minutes

    @Autowired
    public CheckTechniqueByAI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        System.out.println("✅ RestTemplate injecté avec succès dans CheckTechniqueByAI");
    }

    /**
     * Endpoint principal pour l'analyse technique de tous les stocks
     */
    @GetMapping("/all-stocks")
    public ResponseEntity<?> getAllStocksTechnicalAnalysis() {
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("📊 [CheckTechniqueByAI] Analyse technique demandée pour tous les stocks");

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

            System.out.println("✅ [CheckTechniqueByAI] Analyse technique terminée pour " +
                    stocksAnalysis.size() + "/" + allStocks.size() + " stocks en " +
                    (System.currentTimeMillis() - startTime) + "ms");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [CheckTechniqueByAI] Erreur: " + e.getMessage());
            return createServerErrorResponse(e, startTime);
        }
    }

    /**
     * Endpoint pour l'analyse d'un stock spécifique
     */
    @GetMapping("/stock/{stockId}")
    public ResponseEntity<?> getStockTechnicalAnalysis(@PathVariable Integer stockId) {
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("📊 [CheckTechniqueByAI] Analyse technique demandée pour stock ID: " + stockId);

            // Vérifier le cache pour ce stock spécifique
            String cacheKey = "stock-" + stockId;
            if (isCacheValid(cacheKey)) {
                Map<String, Object> cachedAnalysis = analysisCache.get(cacheKey);
                System.out.println("💨 [Cache] Analyse technique servie depuis le cache");
                Map<String, Object> response = new HashMap<>(cachedAnalysis);
                response.put("cached", true);
                response.put("cacheAge", System.currentTimeMillis() - cacheTimestamps.get(cacheKey));
                response.put("responseTime", System.currentTimeMillis() - startTime);
                return ResponseEntity.ok(response);
            }

            // Récupérer les infos du stock
            Map<String, Object> stockInfo = fetchStockInfoFromFastAPI(stockId);
            if (stockInfo.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                        "Stock non trouvé", startTime
                ));
            }

            // Analyser le stock
            Map<String, Object> stockAnalysis = analyzeSingleStock(stockId, stockInfo);

            // Préparer la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stockAnalysis", stockAnalysis);
            response.put("analysisTimestamp", System.currentTimeMillis());
            response.put("responseTime", System.currentTimeMillis() - startTime);
            response.put("message", "Analyse technique générée avec succès");

            // Mettre en cache
            updateCache(cacheKey, response);

            System.out.println("✅ [CheckTechniqueByAI] Analyse technique terminée pour stock " + stockId +
                    " en " + (System.currentTimeMillis() - startTime) + "ms");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [CheckTechniqueByAI] Erreur pour stock " + stockId + ": " + e.getMessage());
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
            stockAnalysis.put("priceHistory", priceHistory.subList(0, Math.min(priceHistory.size(), 10)));
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
     * CALCUL DES INDICATEURS TECHNIQUES COMPLET
     */
    private Map<String, Object> calculateTechnicalIndicators(List<Map<String, Object>> priceHistory) {
        Map<String, Object> indicators = new HashMap<>();

        try {
            if (priceHistory == null || priceHistory.isEmpty()) {
                return indicators;
            }

            // Inverser pour avoir du plus ancien au plus récent
            List<Map<String, Object>> sortedHistory = new ArrayList<>(priceHistory);
            Collections.reverse(sortedHistory);

            // Extraire les prix
            List<Double> prices = sortedHistory.stream()
                    .map(entry -> {
                        Object priceObj = entry.get("prix");
                        if (priceObj instanceof Number) {
                            return ((Number) priceObj).doubleValue();
                        }
                        return 0.0;
                    })
                    .filter(price -> price > 0)
                    .collect(Collectors.toList());

            if (prices.isEmpty()) {
                return indicators;
            }

            double currentPrice = prices.get(prices.size() - 1);
            indicators.put("currentPrice", Math.round(currentPrice * 100.0) / 100.0);

            // Calcul des variations de prix
            if (prices.size() >= 6) {
                double price5DaysAgo = prices.get(prices.size() - 6);
                double priceChange5D = ((currentPrice - price5DaysAgo) / price5DaysAgo) * 100;
                indicators.put("priceChange5D", Math.round(priceChange5D * 100.0) / 100.0);
            }

            if (prices.size() >= 31) {
                double price30DaysAgo = prices.get(prices.size() - 31);
                double priceChange30D = ((currentPrice - price30DaysAgo) / price30DaysAgo) * 100;
                indicators.put("priceChange30D", Math.round(priceChange30D * 100.0) / 100.0);
            }

            // Moving Averages
            Map<String, Object> movingAverages = new HashMap<>();
            if (prices.size() >= 20) {
                double sma20 = calculateSMA(prices, 20);
                movingAverages.put("sma20", Math.round(sma20 * 100.0) / 100.0);
                movingAverages.put("priceVsSma20", Math.round(((currentPrice - sma20) / sma20) * 10000.0) / 100.0);
            }

            if (prices.size() >= 50) {
                double sma50 = calculateSMA(prices, 50);
                movingAverages.put("sma50", Math.round(sma50 * 100.0) / 100.0);
                movingAverages.put("priceVsSma50", Math.round(((currentPrice - sma50) / sma50) * 10000.0) / 100.0);
            }

            if (prices.size() >= 12) {
                double ema12 = calculateEMA(prices, 12);
                movingAverages.put("ema12", Math.round(ema12 * 100.0) / 100.0);
            }

            if (prices.size() >= 26) {
                double ema26 = calculateEMA(prices, 26);
                movingAverages.put("ema26", Math.round(ema26 * 100.0) / 100.0);
            }

            indicators.put("movingAverages", movingAverages);

            // RSI Calculation
            if (prices.size() >= 15) {
                double rsi = calculateRSI(prices, 14);
                Map<String, Object> rsiData = new HashMap<>();
                rsiData.put("value", Math.round(rsi * 100.0) / 100.0);
                rsiData.put("level", rsi > 70 ? "SURACHAT" : rsi < 30 ? "SURVENTE" : "NEUTRE");
                rsiData.put("signal", rsi > 70 ? "VENTE" : rsi < 30 ? "ACHAT" : "NEUTRE");
                indicators.put("rsi", rsiData);
            }

            // MACD Calculation
            if (prices.size() >= 26) {
                Map<String, Object> macd = calculateMACD(prices);
                indicators.put("macd", macd);
            }

            // Bollinger Bands
            if (prices.size() >= 20) {
                Map<String, Object> bollinger = calculateBollingerBands(prices, 20);
                indicators.put("bollingerBands", bollinger);
            }

            // Support and Resistance
            Map<String, Object> supportResistance = calculateSupportResistance(prices);
            indicators.put("supportResistance", supportResistance);

            // Trends
            Map<String, Object> trends = new HashMap<>();
            trends.put("shortTerm", determineTrend(prices, 5));
            trends.put("mediumTerm", determineTrend(prices, 20));
            trends.put("longTerm", determineTrend(prices, Math.min(50, prices.size())));
            indicators.put("trends", trends);

            // Volatility
            double volatility = calculateVolatility(prices, 20);
            Map<String, Object> volatilityData = new HashMap<>();
            volatilityData.put("value", Math.round(volatility * 100.0) / 100.0);
            volatilityData.put("level", volatility > 8.0 ? "ÉLEVÉE" : volatility > 4.0 ? "MOYENNE" : "FAIBLE");
            indicators.put("volatility", volatilityData);

            // Trading Signal
            String tradingSignal = generateTradingSignal(indicators);
            indicators.put("tradingSignal", tradingSignal);

            // Confidence Level
            int confidence = calculateConfidence(prices.size());
            indicators.put("confidence", confidence);

            // Risk Level
            String riskLevel = calculateRiskLevel(volatility, tradingSignal);
            indicators.put("riskLevel", riskLevel);

        } catch (Exception e) {
            System.err.println("❌ Erreur calcul indicateurs techniques: " + e.getMessage());
        }

        return indicators;
    }

    // CALCULS TECHNIQUES

    private double calculateSMA(List<Double> prices, int period) {
        if (prices.size() < period) return 0.0;
        List<Double> recent = prices.subList(prices.size() - period, prices.size());
        return recent.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double calculateEMA(List<Double> prices, int period) {
        double multiplier = 2.0 / (period + 1.0);
        double ema = prices.get(0);

        for (int i = 1; i < prices.size(); i++) {
            ema = (prices.get(i) * multiplier) + (ema * (1 - multiplier));
        }
        return ema;
    }

    private double calculateRSI(List<Double> prices, int period) {
        if (prices.size() <= period) return 50.0;

        List<Double> gains = new ArrayList<>();
        List<Double> losses = new ArrayList<>();

        for (int i = 1; i < prices.size(); i++) {
            double change = prices.get(i) - prices.get(i - 1);
            gains.add(change > 0 ? change : 0.0);
            losses.add(change < 0 ? Math.abs(change) : 0.0);
        }

        double avgGain = gains.subList(0, period).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgLoss = losses.subList(0, period).stream().mapToDouble(Double::doubleValue).average().orElse(0.1);

        for (int i = period; i < gains.size(); i++) {
            avgGain = ((avgGain * (period - 1)) + gains.get(i)) / period;
            avgLoss = ((avgLoss * (period - 1)) + losses.get(i)) / period;
        }

        double rs = avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }

    private Map<String, Object> calculateMACD(List<Double> prices) {
        Map<String, Object> macd = new HashMap<>();

        if (prices.size() < 26) {
            return macd;
        }

        double ema12 = calculateEMA(prices, 12);
        double ema26 = calculateEMA(prices, 26);
        double macdLine = ema12 - ema26;

        // Calcul EMA 9 pour la ligne de signal (simplifié)
        List<Double> macdValues = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            double shortEMA = calculateEMA(prices.subList(0, i + 1), 12);
            double longEMA = calculateEMA(prices.subList(0, i + 1), 26);
            macdValues.add(shortEMA - longEMA);
        }

        double signalLine = calculateEMA(macdValues, 9);
        double histogram = macdLine - signalLine;

        macd.put("macdLine", Math.round(macdLine * 10000.0) / 10000.0);
        macd.put("signalLine", Math.round(signalLine * 10000.0) / 10000.0);
        macd.put("histogram", Math.round(histogram * 10000.0) / 10000.0);
        macd.put("signal", macdLine > signalLine ? "ACHAT" : "VENTE");

        return macd;
    }

    private Map<String, Object> calculateBollingerBands(List<Double> prices, int period) {
        Map<String, Object> bb = new HashMap<>();

        if (prices.size() < period) {
            return bb;
        }

        List<Double> recent = prices.subList(prices.size() - period, prices.size());
        double sma = recent.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double stdDev = Math.sqrt(
                recent.stream()
                        .mapToDouble(price -> Math.pow(price - sma, 2))
                        .average().orElse(0.0)
        );

        double upper = sma + (2 * stdDev);
        double lower = sma - (2 * stdDev);
        double currentPrice = prices.get(prices.size() - 1);

        bb.put("upper", Math.round(upper * 100.0) / 100.0);
        bb.put("middle", Math.round(sma * 100.0) / 100.0);
        bb.put("lower", Math.round(lower * 100.0) / 100.0);
        bb.put("width", Math.round(((upper - lower) / sma) * 10000.0) / 100.0);

        String position;
        if (currentPrice > upper) position = "AU-DESSUS";
        else if (currentPrice < lower) position = "EN-DESSOUS";
        else position = "DANS LA BANDE";

        bb.put("position", position);

        return bb;
    }

    private Map<String, Object> calculateSupportResistance(List<Double> prices) {
        Map<String, Object> sr = new HashMap<>();

        if (prices.size() < 10) {
            return sr;
        }

        double currentPrice = prices.get(prices.size() - 1);
        List<Double> recentPrices = prices.subList(Math.max(0, prices.size() - 20), prices.size());

        double support = recentPrices.stream().mapToDouble(Double::doubleValue).min().orElse(currentPrice * 0.95);
        double resistance = recentPrices.stream().mapToDouble(Double::doubleValue).max().orElse(currentPrice * 1.05);

        sr.put("support", Math.round(support * 100.0) / 100.0);
        sr.put("resistance", Math.round(resistance * 100.0) / 100.0);
        sr.put("distanceToSupport", Math.round(((currentPrice - support) / support) * 10000.0) / 100.0);
        sr.put("distanceToResistance", Math.round(((resistance - currentPrice) / currentPrice) * 10000.0) / 100.0);

        return sr;
    }

    private String determineTrend(List<Double> prices, int period) {
        if (prices.size() < period) return "N/A";

        List<Double> recent = prices.subList(prices.size() - period, prices.size());
        double first = recent.get(0);
        double last = recent.get(recent.size() - 1);

        double change = ((last - first) / first) * 100;

        if (change > 3.0) return "HAUSSIER";
        if (change < -3.0) return "BAISSIER";
        return "LATERAL";
    }

    private double calculateVolatility(List<Double> prices, int period) {
        if (prices.size() < period) return 0.0;

        List<Double> recent = prices.subList(prices.size() - period, prices.size());
        double mean = recent.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = recent.stream()
                .mapToDouble(price -> Math.pow(price - mean, 2))
                .average().orElse(0.0);

        return Math.sqrt(variance) / mean * 100;
    }

    private String generateTradingSignal(Map<String, Object> indicators) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> rsi = (Map<String, Object>) indicators.get("rsi");
            @SuppressWarnings("unchecked")
            Map<String, Object> macd = (Map<String, Object>) indicators.get("macd");
            @SuppressWarnings("unchecked")
            Map<String, Object> bb = (Map<String, Object>) indicators.get("bollingerBands");

            if (rsi == null || macd == null) return "NEUTRE";

            String rsiSignal = (String) rsi.get("signal");
            String macdSignal = (String) macd.get("signal");
            String bbPosition = bb != null ? (String) bb.get("position") : "DANS LA BANDE";

            int buySignals = 0;
            int sellSignals = 0;

            if ("ACHAT".equals(rsiSignal)) buySignals++;
            else if ("VENTE".equals(rsiSignal)) sellSignals++;

            if ("ACHAT".equals(macdSignal)) buySignals++;
            else if ("VENTE".equals(macdSignal)) sellSignals++;

            if ("EN-DESSOUS".equals(bbPosition)) buySignals++;
            else if ("AU-DESSUS".equals(bbPosition)) sellSignals++;

            if (buySignals >= 2 && sellSignals == 0) return "ACHAT FORT";
            if (sellSignals >= 2 && buySignals == 0) return "VENTE FORTE";
            if (buySignals > sellSignals) return "ACHAT FAIBLE";
            if (sellSignals > buySignals) return "VENTE FAIBLE";

            return "NEUTRE";
        } catch (Exception e) {
            return "NEUTRE";
        }
    }

    private int calculateConfidence(int dataPoints) {
        return Math.min(95, 50 + (dataPoints / 2));
    }

    private String calculateRiskLevel(double volatility, String signal) {
        if (volatility > 10.0) return "ÉLEVÉ";
        if (volatility > 5.0) return "MOYEN";
        if ("VENTE FORTE".equals(signal)) return "MOYEN";
        return "FAIBLE";
    }

    /**
     * Analyse avec Gemini AI
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
     * Construction du prompt pour Gemini AI
     */
    private String buildTechnicalAnalysisPrompt(List<Map<String, Object>> priceHistory,
                                                Map<String, Object> indicators,
                                                Map<String, Object> stockInfo) {

        StringBuilder prompt = new StringBuilder();
        prompt.append("Vous êtes un analyste technique expert des marchés financiers. ");
        prompt.append("Analysez les données techniques suivantes et fournissez une analyse concise et professionnelle.\n\n");

        prompt.append("INFORMATIONS SUR LE STOCK:\n");
        prompt.append("- Nom: ").append(stockInfo.get("nom_stock")).append("\n");
        prompt.append("- Prix actuel: ").append(indicators.get("currentPrice")).append(" DT\n");

        prompt.append("\nINDICATEURS TECHNIQUES PRINCIPAUX:\n");

        // RSI
        @SuppressWarnings("unchecked")
        Map<String, Object> rsi = (Map<String, Object>) indicators.get("rsi");
        if (rsi != null) {
            prompt.append("- RSI: ").append(rsi.get("value")).append(" (").append(rsi.get("level")).append(")\n");
        }

        // MACD
        @SuppressWarnings("unchecked")
        Map<String, Object> macd = (Map<String, Object>) indicators.get("macd");
        if (macd != null) {
            prompt.append("- MACD: ").append(macd.get("signal")).append(" (Ligne: ").append(macd.get("macdLine"))
                    .append(", Signal: ").append(macd.get("signalLine")).append(")\n");
        }

        // Bollinger Bands
        @SuppressWarnings("unchecked")
        Map<String, Object> bb = (Map<String, Object>) indicators.get("bollingerBands");
        if (bb != null) {
            prompt.append("- Bollinger Bands: Position ").append(bb.get("position")).append("\n");
        }

        // Tendances
        @SuppressWarnings("unchecked")
        Map<String, Object> trends = (Map<String, Object>) indicators.get("trends");
        if (trends != null) {
            prompt.append("- Tendances: Court=").append(trends.get("shortTerm"))
                    .append(", Moyen=").append(trends.get("mediumTerm"))
                    .append(", Long=").append(trends.get("longTerm")).append("\n");
        }

        // Support/Résistance
        @SuppressWarnings("unchecked")
        Map<String, Object> sr = (Map<String, Object>) indicators.get("supportResistance");
        if (sr != null) {
            prompt.append("- Support: ").append(sr.get("support")).append(" DT\n");
            prompt.append("- Résistance: ").append(sr.get("resistance")).append(" DT\n");
        }

        prompt.append("- Signal de trading: ").append(indicators.get("tradingSignal")).append("\n");
        prompt.append("- Niveau de risque: ").append(indicators.get("riskLevel")).append("\n");
        prompt.append("- Confiance: ").append(indicators.get("confidence")).append("%\n");

        prompt.append("\nDONNÉES DE PRIX RÉCENTES (5 derniers points):\n");
        int count = Math.min(5, priceHistory.size());
        List<Map<String, Object>> recentData = priceHistory.subList(0, count);
        for (Map<String, Object> point : recentData) {
            prompt.append("- ").append(point.get("date_creation"))
                    .append(" : ").append(point.get("prix")).append(" DT\n");
        }

        prompt.append("\nVeuillez fournir une analyse technique structurée qui inclut:\n");
        prompt.append("1. Évaluation de la tendance générale et momentum\n");
        prompt.append("2. Analyse des niveaux de support et résistance clés\n");
        prompt.append("3. Recommandation de trading claire (Achat/Vente/Attente)\n");
        prompt.append("4. Points d'entrée et de sortie potentiels avec objectifs de prix\n");
        prompt.append("5. Niveau de confiance et gestion des risques\n");
        prompt.append("6. Conditions d'invalidation de la recommandation\n\n");

        prompt.append("Répondez en français, soyez concis (max 250 mots) et professionnel. ");
        prompt.append("Utilisez un langage clair adapté aux investisseurs.");

        return prompt.toString();
    }

    // MÉTHODES UTILITAIRES

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

    private Map<String, Object> fetchStockInfoFromFastAPI(Integer stockId) {
        try {
            String url = STOCK_API_BASE_URL + "/stocks/" + stockId;
            Map<String, Object> stockInfo = restTemplate.exchange(
                    url, HttpMethod.GET, null, Map.class
            ).getBody();
            System.out.println("📋 Infos stock récupérées: " + (stockInfo != null ? stockInfo.get("nom_stock") : "N/A"));
            return stockInfo != null ? stockInfo : new HashMap<>();
        } catch (Exception e) {
            System.err.println("❌ Erreur récupération infos stock " + stockId + ": " + e.getMessage());
            return Map.of("nom_stock", "Stock " + stockId, "id_stock", stockId);
        }
    }

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
                return new ArrayList<>();
            } catch (Exception e) {
                System.err.println("❌ Tentative " + attempt + "/" + maxRetries + " échouée pour stock " + stockId + ": " + e.getMessage());
                if (attempt == maxRetries) {
                    return new ArrayList<>();
                }
                try {
                    Thread.sleep(1000 * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return new ArrayList<>();
                }
            }
        }
        return new ArrayList<>();
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
}