import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Server {

        public static void main(String[] args) throws Exception {
                int port = 8081;
                HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
                System.out.println("Starting JCER Smart Assistance server on port " + port);

                server.createContext("/", new StaticFileHandler());
                server.createContext("/api/info", new ApiHandler());

                server.setExecutor(null); // creates a default executor
                server.start();
                System.out.println("Server is running at http://localhost:" + port);
        }

        static class StaticFileHandler implements HttpHandler {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                        String path = exchange.getRequestURI().getPath();
                        if (path.equals("/")) {
                                path = "/index.html";
                        }

                        // Defend against directory traversal
                        if (path.contains("..")) {
                                String response = "403 Forbidden";
                                exchange.sendResponseHeaders(403, response.length());
                                try (OutputStream os = exchange.getResponseBody()) {
                                        os.write(response.getBytes());
                                }
                                return;
                        }

                        java.nio.file.Path filePath = Paths.get("public" + path);
                        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                                String contentType = "text/plain";
                                if (path.endsWith(".html"))
                                        contentType = "text/html";
                                else if (path.endsWith(".css"))
                                        contentType = "text/css";
                                else if (path.endsWith(".js"))
                                        contentType = "application/javascript";
                                else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
                                        contentType = "image/jpeg";
                                else if (path.endsWith(".png"))
                                        contentType = "image/png";
                                else if (path.endsWith(".webp"))
                                        contentType = "image/webp";

                                exchange.getResponseHeaders().set("Content-Type", contentType);
                                exchange.sendResponseHeaders(200, Files.size(filePath));
                                try (OutputStream os = exchange.getResponseBody()) {
                                        Files.copy(filePath, os);
                                }
                        } else {
                                String response = "404 Not Found";
                                exchange.sendResponseHeaders(404, response.length());
                                try (OutputStream os = exchange.getResponseBody()) {
                                        os.write(response.getBytes());
                                }
                        }
                }
        }

        static class ApiHandler implements HttpHandler {
                private final Map<String, Map<String, TopicInfo>> db = new HashMap<>();

        public ApiHandler() {
            // Topic 1: About College
            addTopic("1", "en", "About College",
                    "Jain College of Engineering and Research, Belgaum, Karnataka has rapidly emerged as a premier institution since its establishment in 2010, offering world-class engineering and management education. This dynamic institution is renowned for its commitment to academic excellence, innovative teaching methods, and industry-driven curriculum, making it the perfect choice for students and parents alike. With strong affiliations to VTU Belagavi and AICTE accreditation.",
                    null);
            addTopic("1", "hi", "कॉलेज के बारे में",
                    "जैन कॉलेज ऑफ इंजीनियरिंग एंड रिसर्च, बेलगाम, कर्नाटक 2010 में अपनी स्थापना के बाद से एक प्रमुख संस्थान के रूप में तेजी से उभरा है, जो विश्व स्तरीय इंजीनियरिंग और प्रबंधन शिक्षा प्रदान करता है। वीटीयू बेलगावी और एआईसीટીई मान्यता के साथ, कॉलेज गुणवत्तापूर्ण शिक्षा और प्रगति का प्रतीक है।",
                    null);
            addTopic("1", "kn", "ಕಾಲೇಜಿನ ಬಗ್ಗೆ",
                    "ಜೈನ್ ಕಾಲೇಜ್ ಆಫ್ ಇಂಜಿನಿಯರಿಂಗ್ ಅಂಡ್ ರಿಸರ್ಚ್, ಬೆಳಗಾವಿ, ಕರ್ನಾಟಕವು 2010 ರಲ್ಲಿ ಸ್ಥಾಪನೆಯಾದಂದಿನಿಂದ ವಿಶ್ವದರ್ಜೆಯ ಇಂಜಿನಿಯರಿಂಗ್ ಶಿಕ್ಷಣವನ್ನು ನೀಡುವ ಪ್ರಮುಖ ಸಂಸ್ಥೆಯಾಗಿ ಹೊರಹೊಮ್ಮಿದೆ. ವಿಟಿಯು ಬೆಳಗಾವಿ ಮತ್ತು ಎಐಸಿಟಿಇ ಮಾನ್ಯತೆಯೊಂದಿಗೆ, ಕಾಲೇಜು ಗುಣಮಟ್ಟದ ಶಿಕ್ಷಣದ ಸಂಕೇತವಾಗಿ ನಿಂತಿದೆ.",
                    null);

            // Sub-topic: Principal
            addTopic("1_principal", "en", "Principal Sir",
                    "DOCTOR S. V. Gorabal sir is the Principal of Jain College of Engineering and Research, Belgaum. He is a visionary leader with extensive experience in technical education and research. Under his guidance, the institution has achieved significant milestones in academic excellence and student development.",
                    "images/principale.jpg.png",
                    "Doctor S. V. Gorabal sir is the Principal of Jain College of Engineering and Research, Belgaum. He is a visionary leader with extensive experience in technical education and research. Under his guidance, the institution has achieved significant milestones in academic excellence and student development.");
            addTopic("1_principal", "hi", "प्राचार्य महोदय",
                    "डॉ. एस. वी. गोरबल सर जैन कॉलेज ऑफ इंजीनियरिंग एंड रिसर्च, बेलगाम के प्राचार्य हैं। वह एक दूरदर्शी नेता हैं जिन्हें तकनीकी शिक्षा और अनुसंधान का व्यापक अनुभव है। उनके मार्गदर्शन में, संस्थान ने अकादमिक उत्कृष्टता और छात्र विकास में महत्वपूर्ण मील के पत्थर हासिल किए हैं।",
                    "images/principale.jpg.png");
            addTopic("1_principal", "kn", "ಪ್ರಾಂಶುಪಾಲರು",
                    "ಡಾ. ಎಸ್. ವಿ. ಗೊರಬಾಳ್ ಸರ್ ಅವರು ಬೆಳಗಾವಿಯ ಜೈನ್ ಕಾಲೇಜ್ ಆಫ್ ಇಂಜಿನಿಯರಿಂಗ್ ಅಂಡ್ ರಿಸರ್ಚ್‌ನ ಪ್ರಾಂಶುಪಾಲರಾಗಿದ್ದಾರೆ. ಅವರು ತಾಂತ್ರಿಕ ಶಿಕ್ಷಣ ಮತ್ತು ಸಂಶೋಧನೆಯಲ್ಲಿ ಅಪಾರ ಅನುಭವ ಹೊಂದಿರುವ ದಾರ್ಶನಿಕ ನಾಯಕರಾಗಿದ್ದಾರೆ. ಅವರ ಮಾರ್ಗದರ್ಶನದಲ್ಲಿ, ಸಂಸ್ಥೆಯು ಶೈಕ್ಷಣಿಕ ವಿಭಾಗ ಮತ್ತು ವಿದ್ಯಾರ್ಥಿಗಳ ಅಭಿವೃದ್ಧಿಯಲ್ಲಿ ಗಮನಾರ್ಹ ಸಾಧನೆ ಮಾಡಿದೆ.",
                    "images/principale.jpg.png");

            // Sub-topic: College Faculty (General)
            addTopic("1_faculty", "en", "College Faculty",
                    "JCER boasts a team of highly qualified and dedicated faculty members across all departments. Our educators are committed to student success, research, and holistic development. They bring both industrial and academic expertise to the classroom.",
                    "images/college_faculty.jpg");
            addTopic("1_faculty", "hi", "कॉलेज फैकल्टी",
                    "जेसीईआर के सभी विभागों में उच्च योग्य और समर्पित फैकल्टी सदस्यों की एक टीम है। हमारे शिक्षक छात्रों की सफलता और शोध के लिए प्रतिबद्ध हैं।",
                    "images/college_faculty.jpg");
            addTopic("1_faculty", "kn", "ಕಾಲೇಜು ಬೋಧಕ ವರ್ಗ",
                    "ಜೆಸಿಇಆರ್ ಎಲ್ಲಾ ವಿಭಾಗಗಳಲ್ಲಿ ಉನ್ನತ ಗುಣಮಟ್ಟದ ಬೋಧಕರ ತಂಡವನ್ನು ಹೊಂದಿದೆ. ಅವರು ವಿದ್ಯಾರ್ಥಿಗಳ ಅಭಿವೃದ್ಧಿಗೆ ಮತ್ತು ಸಂಶೋಧನೆಗೆ ಶ್ರಮಿಸುತ್ತಿದ್ದಾರೆ.",
                    "images/college_faculty.jpg");

            // Topic 2: Admission
            addTopic("2", "en", "Admission Process",
                    "Welcome to Jain College of Engineering and Research. We offer Undergraduate and Postgraduate courses in various streams. Admission can be taken through UGCET, DCET, or Management quota. \n\nWe provide courses in:\n• <u>Computer Science & Engineering</u>\n• <u>Electronics & Communication Engineering</u>\n• <u>Artificial Intelligence & Machine Learning</u>\n• <u>Mechanical Engineering</u>\n• <u>Civil Engineering</u>\n• <u>MBA</u>\n\nFor further information, please visit office room number G04 and meet Shivakumar Biradar sir. Thank you.",
                    null);
            addTopic("2", "hi", "प्रवेश प्रक्रिया",
                    "जैन कॉलेज ऑफ इंजीनियरिंग एंड रिसर्च में आपका स्वागत है। हम विभिन्न इंजीनियरिंग धाराओं में स्नातक पाठ्यक्रम प्रदान करते हैं। प्रवेश UGCET, DCET या प्रबंधन कोटे के माध्यम से लिया जा सकता है।\n\nहम निम्नलिखित पाठ्यक्रम प्रदान करते हैं:\n• <u>कंप्यूटर साइंस एंड इंजीनियरिंग</u>\n• <u>इलेक्ट्रॉनिक्स एंड कम्युनिकेशन इंजीनियरिंग</u>\n• <u>आर्टिफिशियल इंटेलिजेंस और मशीन लर्निंग</u>\n• <u>मैकेनिकल इंजीनियरिंग</u>\n• <u>सिविल इंजीनियरिंग</u>\n• <u>एमबीए</u>\n\nअधिक जानकारी के लिए, कृपया कार्यालय कक्ष संख्या G04 में जाएँ और शिवकुमार बिरादर सर से मिलें। धन्यवाद।",
                    null);
            addTopic("2", "kn", "ಪ್ರವೇಶ ಪ್ರಕ್ರಿಯೆ",
                    "ಜೈನ್ ಕಾಲೇಜ್ ಆಫ್ ಇಂಜಿನಿಯರಿಂಗ್ ಅಂಡ್ ರಿಸರ್ಚ್‌ಗೆ ಸುಸ್ವಾಗತ. ನಾವು ವಿವಿಧ ಇಂಜಿನಿಯರಿಂಗ್ ವಿಭಾಗಗಳಲ್ಲಿ ಪದವಿ ಕೋರ್ಸ್‌ಗಳನ್ನು ನೀಡುತ್ತೇವೆ. UGCET, DCET ಅಥವಾ ಮ್ಯಾನೇಜ್‌ಮೆಂಟ್ ಕೋಟಾದ ಮೂಲಕ ಪ್ರವೇಶ ಪಡೆಯಬಹುದು.\n\nನಾವು ಈ ಕೆಳಗಿನ ಕೋರ್ಸ್‌ಗಳನ್ನು ನೀಡುತ್ತೇವೆ:\n• <u><b>ಕಂಪ್ಯೂಟರ್ ಸೈನ್ಸ್ ಅಂಡ್ ಇಂಜಿನಿಯರಿಂಗ್</b></u>\n• <u><b>ಎಲೆಕ್ಟ್ರಾನಿಕ್ಸ್ ಅಂಡ್ ಕಮ್ಯುನಿಕೇಷನ್ ಇಂಜಿನಿಯರಿಂಗ್</b></u>\n• <u><b>ಆರ್ಟಿಫಿಶಿಯಲ್ ಇಂಟೆಲಿಜೆನ್ಸ್ ಮತ್ತು ಮಷಿನ್ ಲರ್ನಿಂಗ್</b></u>\n• <u><b>ಮೆಕ್ಯಾನಿಕಲ್ ಇಂಜಿನಿಯರಿಂಗ್</b></u>\n• <u><b>ಸಿವಿಲ್ ಇಂಜಿನಿಯರಿಂಗ್</b></u>\n• <u><b>ಎಂಬಿಎ</b></u>\n\nಹೆಚ್ಚಿನ ಮಾಹಿತಿಗಾಗಿ, ದಯವಿಟ್ಟು ಕಚೇರಿ ಕೊಠಡಿ ಸಂಖ್ಯೆ G04 ಗೆ ಭೇಟಿ ನೀಡಿ ಮತ್ತು ಶಿವಕುಮಾರ್ ಬಿರಾದಾರ್ ಸರ್ ಅವರನ್ನು ಭೇಟಿ ಮಾಡಿ. ಧನ್ಯವಾದಗಳು.",
                    null);

            addTopic("2_ugcet", "en", "UGCET Admission",
                    "UGCET (KCET) admission is for students seeking entry into the first year of engineering. Candidates must participate in the KEA counseling process based on their CET rank. JCER generally follows the Karnataka government-mandated fee structure for CET (KCET) students, which is approximately ₹90,000 to ₹1 Lakh per year (around ₹3.6 Lakhs - ₹4 Lakhs for 4 years) for tuition, excluding additional college/exam fees. The KCET code for JCER is E269.",
                    null);
            addTopic("2_ugcet", "hi", "UGCET प्रवेश",
                    "UGCET (KCET) प्रवेश उन छात्रों के लिए है जो इंजीनियरिंग के प्रथम वर्ष में प्रवेश लेना चाहते हैं। उम्मीदवारों को अपनी CET रैंक के आधार पर KEA काउंसलिंग प्रक्रिया में भाग लेना होगा। जेसीईआर (JCER) आमतौर पर CET (KCET) छात्रों के लिए कर्नाटक सरकार द्वारा अनिवार्य शुल्क संरचना का पालन करता है, जो शिक्षण शुल्क के लिए लगभग ₹90,000 से ₹1 लाख प्रति वर्ष (4 वर्षों के लिए लगभग ₹3.6 लाख - ₹4 लाख) है, जिसमें अतिरिक्त कॉलेज/परीक्षा शुल्क शामिल नहीं है। जेसीईआर का KCET कोड E269 है।",
                    null);
            addTopic("2_ugcet", "kn", "UGCET ಪ್ರವೇಶ",
                    "UGCET (KCET) ಪ್ರವೇಶವು ಇಂಜಿನಿಯರಿಂಗ್‌ನ ಮೊದಲ ವರ್ಷಕ್ಕೆ ಪ್ರವೇಶ ಪಡೆಯಲು ಇಚ್ಛಿಸುವ ವಿದ್ಯಾರ್ಥಿಗಳಿಗಾಗಿ. ಅಭ್ಯರ್ಥಿಗಳು ತಮ್ಮ CET ರ್‍ಯಾಂಕ್‌ನ ಆಧಾರದ ಮೇಲೆ KEA ಕೌನ್ಸೆಲಿಂಗ್ ಪ್ರಕ್ರಿಯೆಯಲ್ಲಿ ಭಾಗವಹಿಸಬೇಕು. JCER ಸಾಮಾನ್ಯವಾಗಿ CET (KCET) ವಿದ್ಯಾರ್ಥಿಗಳಿಗೆ ಕರ್ನಾಟಕ ಸರ್ಕಾರವು ನಿಗದಿಪಡಿಸಿದ ಶುಲ್ಕ ರಚನೆಯನ್ನು ಅನುಸರಿಸುತ್ತದೆ, ಇದು ಬೋಧನೆಗಾಗಿ (tuition) ವರ್ಷಕ್ಕೆ ಸರಿಸುಮಾರು ₹90,000 ದಿಂದ ₹1 ಲಕ್ಷದವರೆಗೆ ಇರುತ್ತದೆ (4 ವರ್ಷಗಳಿಗೆ ಸುಮಾರು ₹3.6 ಲಕ್ಷದಿಂದ ₹4 ಲಕ್ಷ), ಹೆಚ್ಚುವರಿ ಕಾಲೇಜು/ಪರೀಕ್ಷಾ ಶುಲ್ಕಗಳನ್ನು ಹೊರತುಪಡಿಸಿ. JCER ನ KCET ಕೋಡ್ E269 ಆಗಿದೆ.",
                    null);

            addTopic("2_dcet", "en", "DCET Admission",
                    "DCET admission is for Diploma students seeking lateral entry into the second year (3rd semester) of engineering. Candidates must clear the Diploma CET and participate in counseling. The total fee for the BE {Lateral} course at JCER is approximately ₹3.67 Lakhs to ₹6 Lakhs for the entire duration.",
                    null);
            addTopic("2_dcet", "hi", "DCET प्रवेश",
                    "DCET प्रवेश उन डिप्लोमा छात्रों के लिए है जो इंजीनियरिंग के द्वितीय वर्ष (तीसरे सेमेस्टर) में लेटरल एंट्री के माध्यम से प्रवेश लेना चाहते हैं। उम्मीदवारों को डिप्लोमा CET उत्तीर्ण करना होगा और काउंसलिंग में भाग लेना होगा। JCER में BE (लेटरल) कोर्स के लिए कुल शुल्क पूरी अवधि के लिए लगभग ₹3.67 लाख से ₹6 लाख तक है।",
                    null);
            addTopic("2_dcet", "kn", "DCET ಪ್ರವೇಶ",
                    "DCET ಪ್ರವೇಶವು ಇಂಜಿನಿಯರಿಂಗ್‌ನ ಎರಡನೇ ವರ್ಷಕ್ಕೆ (3ನೇ ಸೆಮಿಸ್ಟರ್) ಲ್ಯಾಟರಲ್ ಎಂಟ್ರಿ ಮೂಲಕ ಪ್ರವೇಶ ಪಡೆಯಲು ಇಚ್ಛಿಸುವ ಡಿಪ್ಲೊಮಾ ವಿದ್ಯಾರ್ಥಿಗಳಿಗಾಗಿ. ಅಭ್ಯರ್ಥಿಗಳು ಡಿಪ್ಲೊಮಾ CET ಯನ್ನು ತೇರ್ಗಡೆ ಹೊಂದಬೇಕು ಮತ್ತು ಕೌನ್ಸೆಲಿಂಗ್‌ನಲ್ಲಿ ಭಾಗವಹಿಸಬೇಕು. JCER ನಲ್ಲಿ BE (ಲ್ಯಾಟರಲ್) ಕೋರ್ಸ್‌ಗೆ ಒಟ್ಟು ಶುಲ್ಕವು ಸಂಪೂರ್ಣ ಅವಧಿಗೆ ಅಂದಾಜು ₹3.67 ಲಕ್ಷದಿಂದ ₹6 ಲಕ್ಷದವರೆಗೆ ಇರುತ್ತದೆ.",
                    null);

            addTopic("2_management", "en", "Management Admission",
                    "For direct admission under Management Quota, please visit the college office at room G04 and meet Shivakumar Biradar sir. You can also contact us at principal@jcer.in for fee details.",
                    null);
            addTopic("2_management", "hi", "मैनेजमेंट प्रवेश",
                    "मैनेजमेंट कोटे के तहत सीधे प्रवेश के लिए, कृपया कॉलेज कार्यालय के कमरा G04 में जाएं और शिवकुमार बिरादर सर से मिलें। आप शुल्क विवरण के लिए हमसे principal@jcer.in पर संपर्क भी कर सकते हैं।",
                    null);
            addTopic("2_management", "kn", "ಮ್ಯಾನೇಜ್‌ಮೆಂಟ್ ಪ್ರವೇಶ",
                    "ಮ್ಯಾನೇಜ್‌ಮೆಂಟ್ ಕೋಟಾದ ಅಡಿಯಲ್ಲಿ ನೇರ ಪ್ರವೇಶಕ್ಕಾಗಿ, ದಯವಿಟ್ಟು ಕಾಲೇಜು ಕಚೇರಿಯ ಕೊಠಡಿ G04 ಗೆ ಭೇಟಿ ನೀಡಿ ಮತ್ತು ಶಿವಕುಮಾರ್ ಬಿರಾದಾರ್ ಸರ್ ಅವರನ್ನು ಭೇಟಿ ಮಾಡಿ. ಶುಲ್ಕದ ವಿವರಗಳಿಗಾಗಿ ನೀವು ನಮ್ಮನ್ನು principal@jcer.in ನಲ್ಲಿ ಸಂಪರ್ಕಿಸಬಹುದು.",
                    null);
                    
            addTopic("2_faculty", "en", "Admission Team",
                    "For admission related queries, please contact our dedicated admission cell led by Shivakumar Biradar sir.",
                    "images/faculty_admission.jpg");
            addTopic("2_faculty", "hi", "प्रवेश टीम",
                    "प्रवेश संबंधी पूछताछ के लिए, कृपया शिवकुमार बिरादर सर के नेतृत्व वाली हमारी समर्पित प्रवेश टीम से संपर्क करें।",
                    "images/faculty_admission.jpg");
            addTopic("2_faculty", "kn", "ಪ್ರವೇಶ ತಂಡ",
                    "ಪ್ರವೇಶಕ್ಕೆ ಸಂಬಂಧಿಸಿದ ವಿಚಾರಣೆಗಾಗಿ, ದಯವಿಟ್ಟು ಶಿವಕುಮಾರ್ ಬಿರಾದಾರ್ ಸರ್ ನೇತೃತ್ವದ ನಮ್ಮ ಪ್ರವೇಶ ತಂಡವನ್ನು ಸಂಪರ್ಕಿಸಿ.",
                    "images/faculty_admission.jpg");

            // Topic 3: CSE
            addTopic("3", "en", "Computer Science & Engineering",
                    "The Department of Computer Science & Engineering was established in 2018 with an intake of 120, and a dedicated Research Center was established in 2024. We are committed to continuously improving the quality of education with highly qualified staff and centralized laboratories featuring the latest software tools. Our four-year undergraduate program provides a solid foundation in CSE principles and emerging technologies, equipping students to solve complex problems and succeed in the IT industry.",
                    "images/cse_dept.jpg.jpeg");
            addTopic("3", "hi", "कंप्यूटर साइंस एंड इंजीनियरिंग",
                    "कंप्यूटर विज्ञान और इंजीनियरिंग विभाग 2018 में 120 की प्रवेश क्षमता के साथ स्थापित किया गया था, और 2024 में एक समर्पित अनुसंधान केंद्र स्थापित किया गया था। हम उच्च योग्य संकाय और नवीनतम सॉफ्टवेयर उपकरणों वाली केंद्रीकृत प्रयोगशालाओं के साथ शिक्षा की गुणवत्ता में निरंतर सुधार के लिए प्रतिबद्ध हैं। हमारा चार साल का स्नातक कार्यक्रम सीएसई सिद्धांतों और उभरती प्रौद्योगिकियों में एक ठोस आधार प्रदान करता है।",
                    "images/cse_dept.jpg.jpeg");
            addTopic("3", "kn", "ಕಂಪ್ಯೂಟರ್ ಸೈನ್ಸ್ ಅಂಡ್ ಇಂಜಿನಿಯರಿಂಗ್",
                    "ಕಂಪ್ಯೂಟರ್ ಸೈನ್ಸ್ ಮತ್ತು ಇಂಜಿನಿಯರಿಂಗ್ ವಿಭಾಗವನ್ನು 2018 ರಲ್ಲಿ 120 ರ ಮೊತ್ತದೊಂದಿಗೆ ಸ್ಥಾಪಿಸಲಾಯಿತು ಮತ್ತು 2024 ರಲ್ಲಿ ಸಂಶೋಧನಾ ಕೇಂದ್ರವನ್ನು ಸ್ಥಾಪಿಸಲಾಯಿತು. ನಾವು ಉನ್ನತ ಗುಣಮಟ್ಟದ ಬೋಧಕರ ತಂಡ ಮತ್ತು ಇತ್ತೀಚಿನ ಸಾಫ್ಟ್‌ವೇರ್ ಪರಿಕರಗಳೊಂದಿಗೆ ಗುಣಮಟ್ಟದ ಶಿಕ್ಷಣವನ್ನು ನೀಡಲು ಬದ್ಧರಾಗಿದ್ದೇವೆ. ನಮ್ಮ ನಾಲ್ಕು ವರ್ಷಗಳ ಪದವಿ ಕಾರ್ಯಕ್ರಮವು ಸಿಎಸ್‌ಇ ತತ್ವಗಳು ಮತ್ತು ಉದಯೋನ್ಮುಖ ತಂತ್ರಜ್ಞಾನಗಳಲ್ಲಿ ಭದ್ರ ಬುನಾದಿಯನ್ನು ಒದಗಿಸುತ್ತದೆ.",
                    "images/cse_dept.jpg.jpeg");
            addTopic("3_faculty", "en", "CSE Faculty Members",
                    "The CSE department features a distinguished team of experts: \n\n" +
                            "1. Doctor Pritam Dhumale (HOD, Professor)\n" +
                            "2. Doctor Jyothi B R (Associate Professor & Dean NAAC)\n" +
                            "3. Doctor Pradnya Malaganve (Associate Professor)\n" +
                            "4. Doctor Raghavendra Katagall (Associate Professor)\n" +
                            "5. Professor Veena B. Mindolli (Assistant Professor)\n" +
                            "6. Professor Bharateesh N. Fadanis (Placement Officer)\n" +
                            "7. Professor Vijayalaxmi S Naganur (Assistant Professor)\n" +
                            "8. Professor Arati Patil (Assistant Professor)\n" +
                            "9. Professor Priyanka Desurkar (Assistant Professor)\n" +
                            "10. Professor Megha V. Patil (Assistant Professor)\n" +
                            "11. Professor Abhilasha J (Assistant Professor)\n" +
                            "12. Professor Vinaya Sarmalkar (Assistant Professor)\n" +
                            "13. Professor Sukhada Inamdar (Assistant Professor)\n" +
                            "14. Professor Suraj R Joshi (Assistant Professor)\n" +
                            "15. Professor Manorma H Patil (Assistant Professor)\n" +
                            "16. Professor Kavya Hullur (Assistant Professor)\n" +
                            "17. Professor Ruthika T Kamble (Assistant Professor)\n" +
                            "18. Professor Rutika Mangaonkar (Assistant Professor)\n" +
                            "19. Professor Swarupa Dhamune (Assistant Professor)\n" +
                            "20. Professor Kusum K Kanbarkar (Assistant Professor)",
                    "images/cse_dept.jpg.jpeg",
                    "The faculty members are: Doctor Pritam Dhumale, Doctor Jyothi B R, Doctor Pradnya Malaganve, Doctor Raghavendra Katagall, Professor Veena B. Mindolli, Professor Bharateesh N. Fadanis, Professor Vijayalaxmi S Naganur, Professor Arati Patil, Professor Priyanka Desurkar, Professor Megha V. Patil, Professor Abhilasha J, Professor Vinaya Sarmalkar, Professor Sukhada Inamdar, Professor Suraj R Joshi, Professor Manorma H Patil, Professor Kavya Hullur, Professor Ruthika T Kamble, Professor Rutika Mangaonkar, Professor Swarupa Dhamune, and Professor Kusum K Kanbarkar.");
            addTopic("3_faculty", "hi", "सीएसई संकाय सदस्य", "सीएसई विभाग के विशेषज्ञ सदस्य:\n\n" +
                    "1. डॉ. प्रीतम धुमाले (एचओडी, प्रोफेसर)\n" +
                    "2. डॉ. ज्योति बी आर (एसोसिएट प्रोफेसर व डीन NAAC)\n" +
                    "3. डॉ. प्रज्ञा मालगांवे (एसोसिएट प्रोफेसर)\n" +
                    "4. डॉ. राघवेंद्र कटगाल (एसोसिएट प्रोफेसर)\n" +
                    "5. प्रो. वीणा बी. मिंडोली (असिस्टेंट प्रोफेसर)\n" +
                    "6. प्रो. भरतीश एन. फडनीस (प्लेसमेंट ऑफिसर)\n" +
                    "7. प्रो. विजयलक्ष्मी एस नागनूर (असिस्टेंट प्रोफेसर)\n" +
                    "8. प्रो. आरती पाटिल (असिस्टेंट प्रोफेसर)\n" +
                    "9. प्रो. प्रियंका देसुरकर (असिस्टेंट प्रोफेसर)\n" +
                    "10. प्रो. मेघा वी. पाटिल (असिस्टेंट प्रोफेसर)\n" +
                    "11. प्रो. अभिलाषा जे (असिस्टेंट प्रोफेसर)\n" +
                    "12. प्रो. विनया सरमलकर (असिस्टेंट प्रोफेसर)\n" +
                    "13. प्रो. सुखदा इनामदार (असिस्टेंट प्रोफेसर)\n" +
                    "14. प्रो. सूरज आर जोशी (असिस्टेंट प्रोफेसर)\n" +
                    "15. प्रो. मनोरमा एच पाटिल (असिस्टेंट प्रोफेसर)\n" +
                    "16. प्रो. काव्या हुल्लूर (असिस्टेंट प्रोफेसर)\n" +
                    "17. प्रो. ऋतिका टी कांबले (असिस्टेंट प्रोफेसर)\n" +
                    "18. प्रो. ऋतिका मनगांवकर (असिस्टेंट प्रोफेसर)\n" +
                    "19. प्रो. स्वरूपा ढमुने (असिस्टेंट प्रोफेसर)\n" +
                    "20. प्रो. कुसुम के कनबरकर (असिस्टेंट प्रोफेसर)", "images/cse_dept.jpg.jpeg");
            addTopic("3_faculty", "kn", "ಸಿಎಸ್ಇ ಬೋಧಕ ವರ್ಗ", "ಸಿಎಸ್‌ಇ ವಿಭಾಗದ ತಜ್ಞ ಸದಸ್ಯರು:\n\n" +
                    "1. ಡಾ. ಪ್ರೀತಮ್ ಧುಮಾಳೆ (ಎಚ್‌ಒಡಿ, ಪ್ರೊಫೆಸರ್)\n" +
                    "2. ಡಾ. ಜ್ಯೋತಿ ಬಿ ಆರ್ (ಅಸೋಸಿಯೇಟ್ ಪ್ರೊಫೆಸರ್ ಮತ್ತು ಡೀನ್ NAAC)\n" +
                    "3. ಡಾ. ಪ್ರಜ್ಞಾ ಮಲಗಾನ್ವೆ (ಅಸೋಸಿಯೇಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "4. ಡಾ. ರಾಘವೇಂದ್ರ ಕಟಗಾಲ್ (ಅಸೋಸಿಯೇಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "5. ಪ್ರೊ. ವೀಣಾ ಬಿ. ಮಿಂದೋಳಿ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "6. ಪ್ರೊ. ಭರತೀಶ್ ಎನ್. ಫಡ್ನಿಸ್ (ಪ್ಲೇಸ್‌ಮೆಂಟ್ ಆಫೀಸರ್)\n" +
                    "7. ಪ್ರೊ. ವಿಜಯಲಕ್ಷ್ಮಿ ಎಸ್ ನಾಗನೂರು (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "8. ಪ್ರೊ. ಆರತಿ ಪಾಟೀಲ್ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "9. ಪ್ರೊ. ಪ್ರಿಯಾಂಕಾ ದೇಸೂರ್ಕರ್ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "10. ಪ್ರೊ. ಮೇಘಾ ವಿ. ಪಾಟೀಲ್ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "11. ಪ್ರೊ. ಅಭಿಲಾಷಾ ಜೆ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "12. ಪ್ರೊ. ವಿನಯಾ ಸರ್ಮಲ್ಕರ್ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "13. ಪ್ರೊ. ಸುಖದಾ ಇನಾಂದಾರ್ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "14. ಪ್ರೊ. ಸೂರಜ್ ಆರ್ ಜೋಶಿ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "15. ಪ್ರೊ. ಮನೋರಮಾ ಹೆಚ್ ಪಾಟೀಲ್ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "16. ಪ್ರೊ. ಕಾವ್ಯ ಹುಲ್ಲೂರ್ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "17. ಪ್ರೊ. ರುತಿಕಾ ಟಿ ಕಾಂಬಳೆ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "18. ಪ್ರೊ. ರುತಿಕಾ ಮನಗಾಂವಕರ್ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "19. ಪ್ರೊ. ಸ್ವರೂಪಾ ಧಮುನೆ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)\n" +
                    "20. ಪ್ರೊ. ಕುಸುಮ್ ಕೆ ಕಣಬರಕರ್ (ಅಸಿಸ್ಟೆಂಟ್ ಪ್ರೊಫೆಸರ್)", "images/cse_dept.jpg.jpeg",
                    "ಬೋಧಕ ವರ್ಗವು ಡಾ. ಪ್ರೀತಮ್ ಧುಮಾಳೆ, ಡಾ. ಜ್ಯೋತಿ ಬಿ ಆರ್, ಡಾ. ಪ್ರಜ್ಞಾ ಮಲಗಾನ್ವೆ, ಡಾ. ರಾಘವೇಂದ್ರ ಕಟಗಾಲ್, ಪ್ರೊ. ವೀಣಾ ಬಿ. ಮಿಂದೋಳಿ, ಪ್ರೊ. ಭರತೀಶ್ ಎನ್. ಫಡ್ನಿಸ್, ಪ್ರೊ. ವಿಜಯಲಕ್ಷ್ಮಿ ಎಸ್ ನಾಗನೂರು, ಪ್ರೊ. ಆರತಿ ಪಾಟೀಲ್, ಪ್ರೊ. ಪ್ರಿಯಾಂಕಾ ದೇಸೂರ್ಕರ್, ಪ್ರೊ. ಮೇಘಾ ವಿ. ಪಾಟೀಲ್, ಪ್ರೊ. ಅಭಿಲಾಷಾ ಜೆ, ಪ್ರೊ. ವಿನಯಾ ಸರ್ಮಲ್ಕರ್, ಪ್ರೊ. ಸುಖದಾ ಇನಾಂದಾರ್, ಪ್ರೊ. ಸೂರಜ್ ಆರ್ ಜೋಶಿ, ಪ್ರೊ. ಮನೋರಮಾ ಹೆಚ್ ಪಾಟೀಲ್, ಪ್ರೊ. ಕಾವ್ಯ ಹುಲ್ಲೂರ್, ಪ್ರೊ. ರುತಿಕಾ ಟಿ ಕಾಂಬಳೆ, ಪ್ರೊ. ರುತಿಕಾ ಮನಗಾಂವಕರ್, ಪ್ರೊ. ಸ್ವರೂಪಾ ಧಮುನೆ, ಮತ್ತು ಪ್ರೊ. ಕುಸುಮ್ ಕೆ ಕಣಬರಕರ್ ಅವರನ್ನು ಒಳಗೊಂಡಿದೆ.");

            // Topic 4: ECE
            addTopic("4", "en", "Electronics & Communication",
                    "The Department of Electronics & Communication Engineering was established in 2018 with an intake of 60, which increased to 90 in 2023. A dedicated Research Center was established in 2024. The department is committed to imparting quality education and fostering innovation in electronics and communication technologies, preparing students for dynamic careers in academia, industry, and research.",
                    "images/ece_dpt.jpg.jpeg");
            addTopic("4", "hi", "इलेक्ट्रॉनिक्स एंड कम्युनिकेशन",
                    "इलेक्ट्रॉनिक्स और संचार इंजीनियरिंग विभाग 2018 में स्थापित किया गया था। यह विभाग गुणवत्तापूर्ण शिक्षा और नवाचार के लिए समर्पित है, जो छात्रों को आधुनिक तकनीकों, अनुसंधान और उद्योग की चुनौतियों के लिए तैयार करता है। 2024 में यहां एक शोध केंद्र भी शुरू किया गया है।",
                    "images/ece_dpt.jpg.jpeg");
            addTopic("4", "kn", "ಎಲೆಕ್ಟ್ರಾನಿಕ್ಸ್ ಅಂಡ್ ಕಮ್ಯುನಿಕೇಷನ್",
                    "ಎಲೆಕ್ಟ್ರಾನಿಕ್ಸ್ ಮತ್ತು ಕಮ್ಯುನಿಕೇಷನ್ ಇಂಜಿನಿಯರಿಂಗ್ ವಿಭಾಗವನ್ನು 2018 ರಲ್ಲಿ ಸ್ಥಾಪಿಸಲಾಯಿತು. ವಿಭಾಗವು ಗುಣಮಟ್ಟದ ಶಿಕ್ಷಣ ಮತ್ತು ನಾವೀನ್ಯತೆಗೆ ಸಮರ್ಪಿತವಾಗಿದೆ, ಇದು ಆಧುನಿಕ ತಂತ್ರಜ್ಞಾನಗಳು, ಸಂಶೋಧನೆ ಮತ್ತು ಉದ್ಯಮದ ಸವಾಲುಗಳಿಗೆ ವಿದ್ಯಾರ್ಥಿಗಳನ್ನು ಸಿದ್ಧಪಡಿಸುತ್ತದೆ.",
                    "images/ece_dpt.jpg.jpeg");
            addTopic("4_faculty", "en", "ECE Faculty Members",
                    "The ECE department features a distinguished team of experts: \n\n" +
                            "1. Professor Chaitanya K. Jambotkar (HOD)\n" +
                            "2. Doctor Virupaxi B. Dalal\n" +
                            "3. Doctor Raghavendra R. Maggavi\n" +
                            "4. Doctor Veeresh M. Hiremath\n" +
                            "5. Doctor Soumya Halagatti\n" +
                            "6. Professor Govinda M R\n" +
                            "7. Professor Vivek Kajagar\n" +
                            "8. Professor Narayana Reddy D\n" +
                            "9. Professor Vijaykumar Patil\n" +
                            "10. Professor Ashwini L. Gudyalakar\n" +
                            "11. Professor Supriya N. Kadrolkar\n" +
                            "12. Professor Rajani V Adikarnataka\n" +
                            "13. Professor Jayashree More\n" +
                            "14. Professor Sneha A. Jagajampi",
                    "images/ece_dpt.jpg.jpeg",
                    "The faculty members are: Professor Chaitanya K. Jambotkar, Doctor Virupaxi B. Dalal, Doctor Raghavendra R. Maggavi, Doctor Veeresh M. Hiremath, Doctor Soumya Halagatti, Professor Govinda M R, Professor Vivek Kajagar, Professor Narayana Reddy D, Professor Vijaykumar Patil, Professor Ashwini L. Gudyalakar, Professor Supriya N. Kadrolkar, Professor Rajani V Adikarnataka, Professor Jayashree More, and Professor Sneha A. Jagajampi.");
            addTopic("4_faculty", "hi", "ईसीई संकाय सदस्य", "ईसीई विभाग के विशेषज्ञ सदस्य:\n\n" +
                    "1. प्रो. चैतन्य के. जंबोटकर (एचओडी)\n" +
                    "2. डॉ. विरूपाक्षी बी. दलाल\n" +
                    "3. डॉ. राघवेंद्र आर. मग्गवी\n" +
                    "4. डॉ. वीरेश एम. हिरेमठ\n" +
                    "5. डॉ. सौम्या हलगट्टी\n" +
                    "6. प्रो. गोविंदा एम आर\n" +
                    "7. प्रो. विवेक काजगर\n" +
                    "8. प्रो. नारायण रेड्डी डी\n" +
                    "9. प्रो. विजयकुमार पाटिल\n" +
                    "10. प्रो. अश्विनी एल. गुडलकर\n" +
                    "11. प्रो. सुप्रिया एन. काद्रोलकर\n" +
                    "12. प्रो. रजनी वी आदिकर्नाटक\n" +
                    "13. प्रो. जयश्री मोरे\n" +
                    "14. प्रो. स्नेहा ए. जगजंपी", "images/ece_dpt.jpg.jpeg");
            addTopic("4_faculty", "kn", "ಇಸಿಇ ಬೋಧಕ ವರ್ಗ", "ಇಸಿಇ ವಿಭಾಗದ ತಜ್ಞ ಸದಸ್ಯರು:\n\n" +
                    "1. ಪ್ರೊ. ಚೈತನ್ಯ ಕೆ. ಜಂಬೋಟ್ಕರ್ (ಎಚ್‌ಒಡಿ)\n" +
                    "2. ಡಾ. ವಿರೂಪಾಕ್ಷಿ ಬಿ. ದಲಾಲ್\n" +
                    "3. ಡಾ. ರಾಘವೇಂದ್ರ ಆರ್. ಮಗ್ಗವಿ\n" +
                    "4. ಡಾ. ವೀರೇಶ್ ಎಂ. ಹಿರೇಮಠ್\n" +
                    "5. ಡಾ. ಸೌಮ್ಯ ಹಲಗಟ್ಟಿ\n" +
                    "6. ಪ್ರೊ. ಗೋವಿಂದ ಎಂ ಆರ್\n" +
                    "7. ಪ್ರೊ. ವಿವೇಕ್ ಕಾಜಗರ\n" +
                    "8. ಪ್ರೊ. ನಾರಾಯಣ ರೆಡ್ಡಿ ಡಿ\n" +
                    "9. ಪ್ರೊ. ವಿಜಯಕುಮಾರ್ ಪಾಟೀಲ್\n" +
                    "10. ಪ್ರೊ. ಅಶ್ವಿನಿ ಎಲ್. ಗುಡಲಕರ್\n" +
                    "11. ಪ್ರೊ. ಸುಪ್ರಿಯಾ ಎನ್. ಕದ್ರೋಲ್ಕರ್\n" +
                    "12. ಪ್ರೊ. ರಜನಿ ವಿ ಆದಿಕರ್ನಾಟಕ\n" +
                    "13. ಪ್ರೊ. ಜಯಶ್ರೀ ಮೋರೆ\n" +
                    "14. ಪ್ರೊ. ಸ್ನೇಹಾ ಎ. ಜಗಜಂಪಿ", "images/ece_dpt.jpg.jpeg");

            // Topic 5: AI & ML
            addTopic("5", "en", "Artificial Intelligence & ML",
                    "The Department of Computer Science & Engineering (Artificial Intelligence and Machine Learning) was established in 2023 with an intake of 60. It is a dynamic and growing hub of innovation at JCER, Belagavi. Despite being new, our department has quickly established itself as a center for learning, offering a cutting-edge curriculum tailored to the ever-evolving fields of AI and ML. Our students are currently in their 7th semester, actively engaging with the latest technologies and hands-on projects.",
                    "images/AIML_dept.jpg.jpeg");
            addTopic("5", "hi", "आर्टिफिशियल इंटेलिजेंस और मशीन लर्निंग",
                    "कंप्यूटर विज्ञान और इंजीनियरिंग विभाग (आर्टिफिशियल इंटेलिजेंस और मशीन लर्निंग) 2023 में 60 की प्रवेश क्षमता के साथ स्थापित किया गया था। यह जेसीईआर, बेलगावी में नवाचार का एक गतिशील और बढ़ता हुआ केंद्र है। नया होने के बावजूद, हमारे विभाग ने खुद को सीखने के केंद्र के रूप में तेजी से स्थापित किया है, जो एआई और एमएल के निरंतर विकसित होने वाले क्षेत्रों के लिए तैयार किया गया एक अत्याधुनिक पाठ्यक्रम पेश करता है। हमारे छात्र वर्तमान में अपने 7वें सेमेस्टर में हैं, जो नवीनतम तकनीकों और व्यावहारिक परियोजनाओं के साथ सक्रिय रूप से जुड़े हुए हैं।",
                    "images/AIML_dept.jpg.jpeg");
            addTopic("5", "kn", "ಆರ್ಟಿಫಿಶಿಯಲ್ ಇಂಟೆಲಿಜೆನ್ಸ್ ಮತ್ತು ಮಷಿನ್ ಲರ್ನಿಂಗ್",
                    "ಕಂಪ್ಯೂಟರ್ ಸೈನ್ಸ್ ಮತ್ತು ಇಂಜಿನಿಯರಿಂಗ್ ವಿಭಾಗವನ್ನು (ಆರ್ಟಿಫಿಶಿಯಲ್ ಇಂಟೆಲಿಜೆನ್ಸ್ ಮತ್ತು ಮಷಿನ್ ಲರ್ನಿಂಗ್) 2023 ರಲ್ಲಿ 60 ರ ಮೊತ್ತದೊಂದಿಗೆ ಸ್ಥಾಪಿಸಲಾಯಿತು. ಇದು ಬೆಳಗಾವಿಯ JCER ನಲ್ಲಿ ನಾವೀನ್ಯತೆಯ ಕ್ರಿಯಾತ್ಮಕ ಮತ್ತು ಬೆಳೆಯುತ್ತಿರುವ ಕೇಂದ್ರವಾಗಿದೆ. ಹೊಸದಾಗಿದ್ದರೂ, ನಮ್ಮ ವಿಭಾಗವು ಕಲಿಕೆಯ ಕೇಂದ್ರವಾಗಿ ವೇಗವಾಗಿ ಸ್ಥಾಪಿತವಾಗಿದೆ, ಇದು AI ಮತ್ತು ML ನ ನಿರಂತರವಾಗಿ ವಿಕಸನಗೊಳ್ಳುತ್ತಿರುವ ಕ್ಷೇತ್ರಗಳಿಗೆ ಅನುಗುಣವಾಗಿ ಅತ್ಯಾಧುನಿಕ ಪಠ್ಯಕ್ರಮವನ್ನು ನೀಡುತ್ತದೆ. ನಮ್ಮ ವಿದ್ಯಾರ್ಥಿಗಳು ಪ್ರಸ್ತುತ ತಮ್ಮ 7 ನೇ ಸೆಮಿಸ್ಟರ್‌ನಲ್ಲಿದ್ದಾರೆ ಮತ್ತು ಇತ್ತೀಚಿನ ತಂತ್ರಜ್ಞಾನಗಳು ಮತ್ತು ಯೋಜನೆಗಳಲ್ಲಿ ತೊಡಗಿಸಿಕೊಂಡಿದ್ದಾರೆ.",
                    "images/AIML_dept.jpg.jpeg");
            addTopic("5_faculty", "en", "AI & ML Faculty Members",
                    "The AI & ML department features a distinguished team of experts: \n\n" +
                            "1. Doctor Prakash K Sonwalkar (HOD)\n" +
                            "2. Doctor Anand Gudnavar\n" +
                            "3. Professor Swati Laxmeshwar\n" +
                            "4. Professor Sneha Khemalapure\n" +
                            "5. Professor Shashikala Reddy\n" +
                            "6. Professor Pallavi P. Dixit\n" +
                            "7. Professor Sakshi Joshi\n" +
                            "8. Professor Varsha Hiremath\n" +
                            "9. Professor Mamata Harogeri",
                    "images/AIML_dept.jpg.jpeg",
                    "The faculty members are: Doctor Prakash K Sonwalkar, Doctor Anand Gudnavar, Professor Swati Laxmeshwar, Professor Sneha Khemalapure, Professor Shashikala Reddy, Professor Pallavi P. Dixit, Professor Sakshi Joshi, Professor Varsha Hiremath, and Professor Mamata Harogeri.");
            addTopic("5_faculty", "hi", "एआई और एमएल संकाय", "एआई और एमएल विभाग के सदस्य:\n\n" +
                    "1. डॉ. प्रकाश के सोनवलकर (एचओडी)\n" +
                    "2. डॉ. आनंद गुडनवर\n" +
                    "3. प्रो. स्वाति लक्ष्मेश्वर\n" +
                    "4. प्रो. स्नेहा खेमलापुरे\n" +
                    "5. प्रो. शशिकला रेड्डी\n" +
                    "6. प्रो. पल्लवी पी. दीक्षित\n" +
                    "7. प्रो. साक्षी जोशी\n" +
                    "8. प्रो. वर्षा हिरेमठ\n" +
                    "9. प्रो. ममता हरोगेरी", "images/AIML_dept.jpg.jpeg");
            addTopic("5_faculty", "kn", "ಎಐ ಮತ್ತು ಎಂಎಲ್ ಬೋಧಕ ವರ್ಗ", "ಎಐ ಮತ್ತು ಎಂಎಲ್ ವಿಭಾಗದ ಸದಸ್ಯರು:\n\n" +
                    "1. ಡಾ. ಪ್ರಕಾಶ್ ಕೆ ಸೋನವಾಲ್ಕರ್ (ಎಚ್‌ಒಡಿ)\n" +
                    "2. ಡಾ. ಆನಂದ್ ಗುಡ್ನವರ್\n" +
                    "3. ಪ್ರೊ. ಸ್ವಾತಿ ಲಕ್ಷ್ಮೆಶ್ವರ್\n" +
                    "4. ಪ್ರೊ. ಸ್ನೇಹ ಖೇಮಲಾಪುರೆ\n" +
                    "5. ಪ್ರೊ. ಶಶಿಕಲಾ ರೆಡ್ಡಿ\n" +
                    "6. ಪ್ರೊ. ಪಲ್ಲವಿ ಪಿ. ದೀಕ್ಷಿತ್\n" +
                    "7. ಪ್ರೊ. ಸಾಕ್ಷಿ ಜೋಷಿ\n" +
                    "8. ಪ್ರೊ. ವರ್ಷಾ ಹಿರೇಮಠ್\n" +
                    "9. ಪ್ರೊ. ಮಮತಾ ಹರೋಗೇರಿ", "images/AIML_dept.jpg.jpeg");

            // Topic 6: Mechanical
            addTopic("6", "en", "Mechanical Engineering",
                    "The Department of Mechanical Engineering was established in 2018 with an intake of 60, and a dedicated Research Center was added in 2024. To facilitate students, the department offers well-equipped laboratories and modern infrastructure. Our highly qualified faculty are dedicated to taking extra initiatives to mold students, preparing them to face the challenges of the competitive world.",
                    "images/mechanical_dpt.jpg.jpeg");
            addTopic("6", "hi", "मैकेनिकल इंजीनियरिंग",
                    "मैकेनिकल इंजीनियरिंग विभाग 2018 में 60 की प्रवेश क्षमता के साथ स्थापित किया गया था, और 2024 में एक समर्पित अनुसंधान केंद्र भी जोड़ा गया था। छात्रों की बेहतर शिक्षा के लिए, विभाग में उच्च तकनीक वाली प्रयोगशालाएं और आधुनिक बुनियादी ढांचा उपलब्ध है। हमारे अनुभवी और उच्च स्तरीय शिक्षक छात्रों को भविष्य की चुनौतियों के लिए तैयार करने और उनके सर्वांगीण विकास के लिए निरंतर प्रयास करते हैं।",
                    "images/mechanical_dpt.jpg.jpeg");
            addTopic("6", "kn", "ಮೆಕ್ಯಾನಿಕಲ್ ಇಂಜಿನಯರಿಂಗ್",
                    "ಮೆಕ್ಯಾನಿಕಲ್ ಇಂಜಿನಿಯರಿಂಗ್ ವಿಭಾಗವನ್ನು 2018 ರಲ್ಲಿ 60 ವಿದ್ಯಾರ್ಥಿಗಳ ಸಾಮರ್ಥ್ಯದೊಂದಿಗೆ ಸ್ಥಾಪಿಸಲಾಯಿತು ಮತ್ತು 2024 ರಲ್ಲಿ ಸಂಶೋಧನಾ ಕೇಂದ್ರವನ್ನು ಪ್ರಾರಂಭಿಸಲಾಯಿತು. ವಿದ್ಯಾರ್ಥಿಗಳಿಗೆ ಉತ್ತಮ ಪ್ರಾಯೋಗಿಕ ಜ್ಞಾನ ನೀಡಲು ವಿಭಾಗವು ಅತ್ಯಾಧುನಿಕ ಪ್ರಯೋಗಾಲಯಗಳು ಮತ್ತು ಆಧುನಿಕ ಮೂಲಸೌಕರ್ಯಗಳನ್ನು ಹೊಂದಿದೆ. ನಮ್ಮ ಅನುಭವಿ ಬೋಧಕ ವರ್ಗವು ವಿದ್ಯಾರ್ಥಿಗಳನ್ನು ಜಾಗತಿಕ ಸ್ಪರ್ಧೆ ಎದುರಿಸಲು ಸಿದ್ಧಪಡಿಸುವಲ್ಲಿ ಸದಾ ಶ್ರಮಿಸುತ್ತದೆ.",
                    "images/mechanical_dpt.jpg.jpeg");
            addTopic("6_faculty", "en", "Mechanical Faculty Members",
                    "The Mechanical Engineering department features a distinguished team of experts: \n\n" +
                            "1. Doctor S.V. Gorabal (Principal)\n" +
                            "2. Doctor K. B. Jagadeeshgouda (HOD)\n" +
                            "3. Doctor Gangadhar M Kanaginahal\n" +
                            "4. Doctor Mallikarjun Jalageri\n" +
                            "5. Doctor Sadashiv Bellubbi\n" +
                            "6. Professor Karthik R\n" +
                            "7. Professor Rajshekar P Biradar\n" +
                            "8. Professor Praveen Patil\n" +
                            "9. Professor Prasanna Mangaonkar\n" +
                            "10. Professor Ganesh R. C\n" +
                            "11. Professor Santosh A\n" +
                            "12. Professor Sachin Kallannavar",
                    "images/mechanical_dpt.jpg.jpeg",
                    "The faculty members are: Doctor S.V. Gorabal, Doctor K. B. Jagadeeshgouda, Doctor Gangadhar M Kanaginahal, Doctor Mallikarjun Jalageri, Doctor Sadashiv Bellubbi, Professor Karthik R, Professor Rajshekar P Biradar, Professor Praveen Patil, Professor Prasanna Mangaonkar, Professor Ganesh R. C, Professor Santosh A, and Professor Sachin Kallannavar.");
            addTopic("6_faculty", "hi", "मैकेनिकल संकाय सदस्य", "मैकेनिकल विभाग के सदस्य:\n\n" +
                    "1. डॉ. एस.वी. गोरबल (प्राचार्य)\n" +
                    "2. डॉ. के. बी. जगदीशगौड़ा (एचओडी)\n" +
                    "3. डॉ. गंगाधर एम कनागिनाहल\n" +
                    "4. डॉ. मल्लिकार्जुन जलगरी\n" +
                    "5. डॉ. सदाशिव बेलुब्बी\n" +
                    "6. प्रो. कार्तिक आर\n" +
                    "7. प्रो. राजशेखर पी बिरादर\n" +
                    "8. प्रो. प्रवीण पाटिल\n" +
                    "9. प्रो. प्रसन्न मनगांवकर\n" +
                    "10. प्रो. गणेश आर. सी\n" +
                    "11. प्रो. संतोष ए\n" +
                    "12. प्रो. सचिन कल्लन्नवर", "images/mechanical_dpt.jpg.jpeg");
            addTopic("6_faculty", "kn", "ಮೆಕ್ಯಾನಿಕಲ್ ಬೋಧಕ ವರ್ಗ", "ಮೆಕ್ಯಾನಿಕಲ್ ವಿಭಾಗದ ಸದಸ್ಯರು:\n\n" +
                    "1. ಡಾ. ಎಸ್.ವಿ. ಗೊರಬಾಳ್ (ಪ್ರಾಂಶುಪಾಲರು)\n" +
                    "2. ಡಾ. ಕೆ. ಬಿ. ಜಗದೀಶಗೌಡ (ಎಚ್‌ಒಡಿ)\n" +
                    "3. ಡಾ. ಗಂಗಾಧರ್ ಎಂ ಕಣಗಿನಹಾಳ\n" +
                    "4. ಡಾ. ಮಲ್ಲಿಕಾರ್ಜುನ್ ಜಲಗೇರಿ\n" +
                    "5. ಡಾ. ಸದಾಶಿವ ಬೆಳ್ಳುಬ್ಬಿ\n" +
                    "6. ಪ್ರೊ. ಕಾರ್ತಿಕ್ ಆರ್\n" +
                    "7. ಪ್ರೊ. ರಾಜಶೇಖರ್ ಪಿ ಬಿರಾದಾರ್\n" +
                    "8. ಪ್ರೊ. ಪ್ರವೀಣ್ ಪಾಟೀಲ್\n" +
                    "9. ಪ್ರೊ. ಪ್ರಸನ್ನ ಮನಗಾಂವಕರ್\n" +
                    "10. ಪ್ರೊ. ಗಣೇಶ್ ಆರ್. ಸಿ\n" +
                    "11. ಪ್ರೊ. ಸಂತೋಷ್ ಎ\n" +
                    "12. ಪ್ರೊ. ಸಚಿನ್ ಕಲ್ಲಣ್ಣವರ್", "images/mechanical_dpt.jpg.jpeg");

            // Topic 7: Civil
            addTopic("7", "en", "Civil Engineering",
                    "The Department of Civil Engineering was established in 2018 with an intake of 30. As a professional discipline, it deals with the design, construction, and maintenance of the physical and naturally built environment, including roads, bridges, canals, dams, and airports. Being the second-oldest engineering discipline, it plays a vital role in both the public and private sectors, serving everyone from individual homeowners to international companies.",
                    "images/civil_dept.jpg.jpeg");
            addTopic("7", "hi", "सिविल इंजीनियरिंग",
                    "सिविल इंजीनियरिंग विभाग 2018 में 30 की प्रवेश क्षमता के साथ स्थापित किया गया था। एक पेशेवर अनुशासन के रूप में, यह सड़कों, पुलों, नहरों, बांधों और हवाई अड्डों सहित भौतिक और प्राकृतिक रूप से निर्मित पर्यावरण के डिजाइन, निर्माण और रखरखाव से संबंधित है। दूसरी सबसे पुरानी इंजीनियरिंग विधा होने के नाते, यह सार्वजनिक और निजी दोनों क्षेत्रों में महत्वपूर्ण भूमिका निभाती है।",
                    "images/civil_dept.jpg.jpeg");
            addTopic("7", "kn", "ಸಿವಿಲ್ ಇಂಜಿನಿಯರಿಂಗ್",
                    "ಸಿವಿಲ್ ಇಂಜಿನಿಯರಿಂಗ್ ವಿಭಾಗವನ್ನು 2018 ರಲ್ಲಿ 30 ರ ಮೊತ್ತದೊಂದಿಗೆ ಸ್ಥಾಪಿಸಲಾಯಿತು. ವೃತ್ತಿಪರ ಶಿಸ್ತಿನಂತೆ, ಇದು ರಸ್ತೆಗಳು, ಸೇತುವೆಗಳು, ಕಾಲುವೆಗಳು, ಅಣೆಕಟ್ಟುಗಳು ಮತ್ತು ವಿಮಾನ ನಿಲ್ದಾಣಗಳನ್ನು ಒಳಗೊಂಡಂತೆ ಭೌತಿಕ ಮತ್ತು ನೈಸರ್ಗಿಕವಾಗಿ ನಿರ್ಮಿಸಲಾದ ಪರಿಸರದ ವಿನ್ಯಾಸ, ನಿರ್ಮಾಣ ಮತ್ತು ನಿರ್ವಹಣೆಯನ್ನು ನಿರ್ವಹಿಸುತ್ತದೆ. ಇದು ಸಾರ್ವಜನಿಕ ಮತ್ತು ಖಾಸಗಿ ವಲಯಗಳಲ್ಲಿ ಪ್ರಮುಖ ಪಾತ್ರ ವಹಿಸುತ್ತದೆ.",
                    "images/civil_dept.jpg.jpeg");
            addTopic("7_faculty", "en", "Civil Faculty Members",
                    "The Civil Engineering department features a distinguished team of experts: \n\n" +
                            "1. Professor Tungesh Naidu (HOD, Assistant Professor)\n" +
                            "2. Doctor Rajkumar V Raikar (Professor)\n" +
                            "3. Doctor R. Shreedhar (Professor)\n" +
                            "4. Professor Rahul R. Bannur (Assistant Professor)\n" +
                            "5. Professor Monika B (Assistant Professor)\n" +
                            "6. Professor Sheetal P N (Assistant Professor)\n" +
                            "7. Professor Soujanya V K (Assistant Professor)\n" +
                            "8. Professor Riya D. Kangralkar (Assistant Professor)\n" +
                            "9. Professor Ganesh Sayanekar (Assistant Professor)",
                    "images/civil_dept.jpg.jpeg",
                    "The faculty members are: Professor Tungesh Naidu, Doctor Rajkumar V Raikar, Doctor R. Shreedhar, Professor Rahul R. Bannur, Professor Monika B, Professor Sheetal P N, Professor Soujanya V K, Professor Riya D. Kangralkar, and Professor Ganesh Sayanekar.");
            addTopic("7_faculty", "hi", "सिविल संकाय सदस्य", "सिविल विभाग के विशेषज्ञ सदस्य:\n\n" +
                    "1. प्रो. तुंगेश नायडू (एचओडी)\n" +
                    "2. डॉ. राजकुमार वी रायकर\n" +
                    "3. डॉ. आर. श्रीधर\n" +
                    "4. प्रो. राहुल आर. बन्नूर\n" +
                    "5. प्रो. मोनिका बी\n" +
                    "6. प्रो. शीतल पी एन\n" +
                    "7. प्रो. सौजन्या वी के\n" +
                    "8. प्रो. रिया डी. केंग्रलकर\n" +
                    "9. प्रो. गणेश सायनेकर", "images/civil_dept.jpg.jpeg");
            addTopic("7_faculty", "kn", "ಸಿವಿಲ್ ಬೋಧಕ ವರ್ಗ", "ಸಿವಿಲ್ ವಿಭಾಗದ ತಜ್ಞ ಸದಸ್ಯರು:\n\n" +
                    "1. ಪ್ರೊ. ತುಂಗೇಶ್ ನಾಯ್ಡು (ಎಚ್‌ಒಡಿ)\n" +
                    "2. ಡಾ. ರಾಜಕುಮಾರ್ ವಿ ರೈಕರ್\n" +
                    "3. ಡಾ. ಆರ್. ಶ್ರೀಧರ್\n" +
                    "4. ಪ್ರೊ. ರಾಹುಲ್ ಆರ್. ಬನ್ನೂರು\n" +
                    "5. ಪ್ರೊ. ಮೋನಿಕಾ ಬಿ\n" +
                    "6. ಪ್ರೊ. ಶೀತಲ್ ಪಿ ಎನ್\n" +
                    "7. ಪ್ರೊ. ಸೌಜನ್ಯ ವಿ ಕೆ\n" +
                    "8. ಪ್ರೊ. ರಿಯಾ ಡಿ. ಕಾಂಗ್ರಾಲ್ಕರ್\n" +
                    "9. ಪ್ರೊ. ಗಣೇಶ್ ಸಾಯನೇಕರ್", "images/civil_dept.jpg.jpeg");

            // Topic 8: Applied Science
            addTopic("8", "en", "Applied Science",
                    "The Basic Science Department is dedicated to laying a robust foundation in core principles of science, shaping the analytical and problem-solving skills of future engineers. Our team of highly qualified faculty is passionate about nurturing curiosity and academic excellence. Equipped with state-of-the-art laboratories and advanced teaching methodologies, we ensure an engaging learning experience. This solid grounding empowers students to excel throughout their engineering journey, setting the stage for innovative thinking and success in a technologically driven world.",
                    "images/Appscience_dept.jpg.jpeg");
            addTopic("8", "hi", "एप्लाइड साइंस",
                    "बेसिक साइंस विभाग इंजीनियरिंग की नींव रखने के लिए समर्पित है। हम भविष्य के इंजीनियरों के विश्लेषणात्मक कौशल को विकसित करने पर ध्यान केंद्रित करते हैं।",
                    "images/Appscience_dept.jpg.jpeg");
            addTopic("8", "kn", "ಅಪ್ಲೈಡ್ ಸೈನ್ಸ್",
                    "ಬೇಸಿಕ್ ಸೈನ್ಸ್ ವಿಭಾಗವು ಇಂಜಿನಿಯರಿಂಗ್‌ನ ಅಡಿಪಾಯ ಹಾಕಲು ಸಮರ್ಪಿತವಾಗಿದೆ. ಭವಿಷ್ಯದ ಇಂಜಿನಿಯರ್‌ಗಳ ವಿಶ್ಲೇಷಣಾತ್ಮಕ ಕೌಶಲ್ಯಗಳನ್ನು ಅಭಿವೃದ್ಧಿಪಡಿಸುವತ್ತ ನಾವು ಗಮನ ಹರಿಸುತ್ತೇವೆ.",
                    "images/Appscience_dept.jpg.jpeg");
            addTopic("8_faculty", "en", "Applied Science Faculty Members",
                    "The Applied Science department features a distinguished team of experts: \n\n" +
                            "1. Doctor Raghu Gunnagol (HOD)\n" +
                            "2. Professor Shweta Hiremath\n" +
                            "3. Professor Madhuri Bahadduri\n" +
                            "4. Professor Priyadarshini Udapudi\n" +
                            "5. Professor Shweta Patil\n" +
                            "6. Professor Aishwarya Dhamanekar\n" +
                            "7. Professor Tejashwini K Samay\n" +
                            "8. Professor G. S. Patil\n" +
                            "9. Professor Jasmith Charantimath\n" +
                            "10. Professor Vijaylaxmi Hulikatti\n" +
                            "11. Professor Sandhya Kambar\n" +
                            "12. Mister Anand T Salutigikar\n" +
                            "13. Professor Smita Shebannavar\n" +
                            "14. Professor Sheldon Telles\n" +
                            "15. Professor Shridhar S. Dharmatti",
                    "images/Appscience_dept.jpg.jpeg",
                    "The faculty members are: Doctor Raghu Gunnagol, Professor Shweta Hiremath, Professor Madhuri Bahadduri, Professor Priyadarshini Udapudi, Professor Shweta Patil, Professor Aishwarya Dhamanekar, Professor Tejashwini K Samay, Professor G. S. Patil, Professor Jasmith Charantimath, Professor Vijaylaxmi Hulikatti, Professor Sandhya Kambar, Mister Anand T Salutigikar, Professor Smita Shebannavar, Professor Sheldon Telles, and Professor Shridhar S. Dharmatti.");
            addTopic("8_faculty", "hi", "एप्लाइड साइंस संकाय", "एप्लाइड साइंस विभाग सदस्य:\n\n" +
                    "1. डॉ. रघु गुणागोल (एचओडी)\n" +
                    "2. प्रो. श्वेता हिरेमठ\n" +
                    "3. प्रो. माधुरी बहादुरि\n" +
                    "4. प्रो. प्रियदर्शिनी उदापुडी\n" +
                    "5. प्रो. श्वेता पाटिल\n" +
                    "6. प्रो. ऐश्वर्या धमनेकर\n" +
                    "7. प्रो. तेजस्विनी के समय\n" +
                    "8. प्रो. जी. एस. पाटिल\n" +
                    "9. प्रो. जस्मिथ चरणतीमठ\n" +
                    "10. प्रो. विजयलक्ष्मी हुलीकट्टी\n" +
                    "11. प्रो. संध्या कंबार\n" +
                    "12. मिस्टर आनंद टी सालुटीगीकर\n" +
                    "13. प्रो. स्मिता शेबन्नवर\n" +
                    "14. प्रो. शेल्डन टेलेस\n" +
                    "15. प्रो. श्रीधर एस. धर्माट्टी", "images/Appscience_dept.jpg.jpeg");
            addTopic("8_faculty", "kn", "ಅಪ್ಲೈಡ್ ಸೈನ್ಸ್ ಬೋಧಕ ವರ್ಗ", "ಅಪ್ಲೈಡ್ ಸೈನ್ಸ್ ವಿಭಾಗದ ಸದಸ್ಯರು:\n\n" +
                    "1. ಡಾ. ರಘು ಗುಣಗೋಳ (ಎಚ್‌ಒಡಿ)\n" +
                    "2. ಪ್ರೊ. ಶ್ವೇತಾ ಹಿರೇಮಠ್\n" +
                    "3. ಪ್ರೊ. ಮಾಧುರಿ ಬಹದ್ದೂರಿ\n" +
                    "4. ಪ್ರೊ. ಪ್ರಿಯದರ್ಶಿನಿ ಉದಪುಡಿ\n" +
                    "5. ಪ್ರೊ. ಶ್ವೇತಾ ಪಾಟೀಲ್\n" +
                    "6. ಪ್ರೊ. ಐಶ್ವರ್ಯ ಧಮಣೇಕರ್\n" +
                    "7. ಪ್ರೊ. ತೇಜಸ್ವಿನಿ ಕೆ ಸಮಯ್\n" +
                    "8. ಪ್ರೊ. ಜಿ.ಎಸ್. ಪಾಟೀಲ್\n" +
                    "9. ಪ್ರೊ. ಜಸ್ಮಿತ್ ಚರಂತಿಮಠ್\n" +
                    "10. ಪ್ರೊ. ವಿಜಯಲಕ್ಷ್ಮಿ ಹುಲಿಕಟ್ಟಿ\n" +
                    "11. ಪ್ರೊ. ಸಂಧ್ಯಾ ಕಂಬಾರ್\n" +
                    "12. ಮಿಸ್ಟರ್ ಆನಂದ್ ಟಿ ಸಾಲುಟಿಗೀಕರ್\n" +
                    "13. ಪ್ರೊ. ಸ್ಮಿತಾ ಶೇಬಣ್ಣವರ್\n" +
                    "14. ಪ್ರೊ. ಶೆಲ್ಡನ್ ಟೆಲ್ಲೆಸ್\n" +
                    "15. ಪ್ರೊ. ಶ್ರೀಧರ್ ಎಸ್. ಧರ್ಮಟ್ಟಿ", "images/Appscience_dept.jpg.jpeg");

            // Topic 9: MBA
            addTopic("9", "en", "MBA Department",
                    "Established in 2024, our dynamic Department of MBA focuses on Digital Disruption, Analytics, and Modern Management practices. The department aims to develop visionary leaders equipped with contemporary management skills and an ethical mindset.",
                    "images/MBA_dept.jpg.jpeg");
            addTopic("9", "hi", "एमबीए विभाग",
                    "2024 में स्थापित हमारा एमबीए विभाग आधुनिक प्रबंधन प्रथाओं और डिजिटल नवाचार पर केंद्रित है।",
                    "images/MBA_dept.jpg.jpeg");
            addTopic("9", "kn", "ಎಂಬಿಎ ವಿಭಾಗ",
                    "2024 ರಲ್ಲಿ ಸ್ಥಾಪನೆಯಾದ ನಮ್ಮ ಎಂಬಿಎ ವಿಭಾಗವು ಆಧುನಿಕ ನಿರ್ವಹಣಾ ಪದ್ಧತಿಗಳು ಮತ್ತು ಡಿಜಿಟಲ್ ನಾವೀನ್ಯತೆಗಳ ಮೇಲೆ ಕೇಂದ್ರೀಕರಿಸುತ್ತದೆ.",
                    "images/MBA_dept.jpg.jpeg");
            addTopic("9_faculty", "en", "MBA Faculty Members",
                    "The MBA department features a distinguished team of experts: \n\n" +
                            "1. Doctor Indrajit Doddanavar\n" +
                            "2. Doctor Vrushali Pakhannavar\n" +
                            "3. Mister Sangam B. Jadhav\n" +
                            "4. CA. Vinayak Asundi\n" +
                            "5. Professor Sandhya Sherigar\n" +
                            "6. Mister Pavankumar Ramgouda",
                    "images/MBA_dept.jpg.jpeg",
                    "The faculty members are: Doctor Indrajit Doddanavar, Doctor Vrushali Pakhannavar, Mister Sangam B. Jadhav, CA Vinayak Asundi, Professor Sandhya Sherigar, and Mister Pavankumar Ramgouda.");
            addTopic("9_faculty", "hi", "एमबीए संकाय सदस्य", "एमबीए विभाग सदस्य:\n\n" +
                    "1. डॉ. इंद्रजीत दोड्डनवर\n" +
                    "2. डॉ. वृषाली पखनवर\n" +
                    "3. मिस्टर संगम जाधव\n" +
                    "4. सीए विनायक असुंडी\n" +
                    "5. प्रो. संध्या शेरिगर\n" +
                    "6. मिस्टर पवनकुमार रामगौड़ा", "images/MBA_dept.jpg.jpeg");
            addTopic("9_faculty", "kn", "ಎಂಬಿಎ ಬೋಧಕ ವರ್ಗ", "ಎಂಬಿಎ ವಿಭಾಗದ ಸದಸ್ಯರು:\n\n" +
                    "1. ಡಾ. ಇಂದ್ರಜಿತ್ ದೊಡ್ಡನವರ್\n" +
                    "2. ಡಾ. ವೃಶಾಲಿ ಪಖನ್ನವರ್\n" +
                    "3. ಮಿಸ್ಟರ್ ಸಂಗಮ್ ಬಿ ಜಾದವ್\n" +
                    "4. ಸಿಎ ವಿನಾಯಕ್ ಅಸುಂಡಿ\n" +
                    "5. ಪ್ರೊ. ಸಂಧ್ಯಾ ಶೇರಿಗಾರ್\n" +
                    "6. ಮಿಸ್ಟರ್ ಪವನಕುಮಾರ್ ರಾಮಗೌಡ", "images/MBA_dept.jpg.jpeg");

            // Topic 10: Rules
            addTopic("10", "en", "College Rules",
                    "Character building is our main aim. Discipline, attendance, and ethics are strictly enforced at JCER.",
                    null);
            addTopic("10", "hi", "कॉलेज के नियम",
                    "अनुशासन, उपस्थिति और नैतिकता जेसीईआर में दृढ़ता से लागू की जाती है। छात्र का सर्वांगीण विकास हमारा मुख्य लक्ष्य है।",
                    null);
            addTopic("10", "kn", "ಕಾಲೇಜು ನಿಯಮಗಳು",
                    "ಶಿಸ್ತು, ಹಾಜರಾತಿ ಮತ್ತು ನೈತಿಕತೆಯನ್ನು ಜೆಸಿಇಆರ್ ನಲ್ಲಿ ಕಟ್ಟುನಿಟ್ಟಾಗಿ ಜಾರಿಗೊಳಿಸಲಾಗಿದೆ. ವಿದ್ಯಾರ್ಥಿಯ ಸರ್ವತೋಮುಖ ಅಭಿವೃದ್ಧಿ ನಮ್ಮ ಮುಖ್ಯ ಗುರಿಯಾಗಿದೆ.",
                    null);

            // Topic 11: Canteen
            addTopic("11", "en", "Canteen",
                    "Our college canteen is very clean and well maintained. The surroundings are neat, and proper hygiene is followed while preparing food. Clean water is available, and the seating area is comfortable for students to sit and relax.\n\nThe food in our canteen is very tasty and fresh. Different types of snacks, meals, and drinks are available.",
                    null);
            addTopic("11", "hi", "कैंटीन",
                    "हमारा कॉलेज कैंटीन बहुत साफ और स्वच्छ है। हम छात्रों के लिए ताजा और स्वादिष्ट भोजन और बैठने के लिए आरामदायक जगह प्रदान करते हैं।",
                    null);
            addTopic("11", "kn", "ಕ್ಯಾಂಟೀನ್",
                    "ನಮ್ಮ ಕಾಲೇಜು ಕ್ಯಾಂಟೀನ್ ತುಂಬಾ ಸ್ವಚ್ಛ ಮತ್ತು ನೈರ್ಮಲ್ಯದಿಂದ ಕೂಡಿದೆ. ನಾವು ವಿದ್ಯಾರ್ಥಿಗಳಿಗೆ ತಾಜಾ ಮತ್ತು ರುಚಿಕರವಾದ ಆಹಾರ ಮತ್ತು ಕುಳಿತುಕೊಳ್ಳಲು ಆರಾಮದಾಯಕವಾದ ಸ್ಥಳವನ್ನು ಒದಗಿಸುತ್ತೇವೆ.",
                    null);

            // Topic 12: Facilities
            addTopic("12", "en", "Campus Facilities",
                    "JCER offers world-class infrastructure to support your academic journey: \n\n" +
                            "• Well-equipped Laboratories in each department\n" +
                            "• Modern Computer Centre with latest machines and software\n" +
                            "• Transportation with 7+ buses\n" +
                            "• Centralized Computing facility with 200+ computers on network\n" +
                            "• 300 Mbps Internet connectivity, Wi-fi, CCTV surveillance\n" +
                            "• Ultra-modern Library (10,000+ volumes, E-Library)\n" +
                            "• Training & Placement Cell\n" +
                            "• Outdoor & Indoor Sports facilities\n" +
                            "• Digital Classrooms with video conferencing\n" +
                            "• IT Incubation Centre\n" +
                            "• Well maintained canteens and mess\n" +
                            "• Provisions for easy access (Ramps & Lifts)",
                    "images/Facility.jpg.jpeg",
                    "JCER provides world-class infrastructure including well-equipped laboratories, modern computer centers, transportation, high-speed internet, an ultra-modern library, digital classrooms, and excellent sports facilities.");
            addTopic("12", "hi", "कैंपस सुविधाएं",
                    "जेसीईआर विश्व स्तरीय बुनियादी ढांचा प्रदान करता है, जिसमें आधुनिक प्रयोगशालाएं, उच्च गति इंटरनेट, एक विशाल पुस्तकालय और खेल सुविधाएं शामिल हैं।",
                    "images/Facility.jpg.jpeg");
            addTopic("12", "kn", "ಕ್ಯಾಂಪಸ್ ಸೌಲಭ್ಯಗಳು",
                    "ಜೆಸಿಇಆರ್ ಆಧುನಿಕ ಪ್ರಯೋಗಾಲಯಗಳು, ಹೈ-ಸ್ಪೀಡ್ ಇಂಟರ್ನೆಟ್, ಬೃಹತ್ ಗ್ರಂಥಾಲಯ ಮತ್ತು ಕ್ರೀಡಾ ಸೌಲಭ್ಯಗಳನ್ನು ಒಳಗೊಂಡಂತೆ ವಿಶ್ವದರ್ಜೆಯ ಮೂಲಸೌಕರ್ಯಗಳನ್ನು ಒದಗಿಸುತ್ತದೆ.",
                    "images/Facility.jpg.jpeg");

            // Topic 13: Placements
            addTopic("13", "en", "Placements",
                    "JCER offers a dedicated Placement Cell that provides robust training and placement opportunities for all our students. We have consistently achieved excellent placement records with top multinational companies. Based on 2025 placement reports, the Jain Deemed-to-be University (Faculty of Engineering and Technology) in Bangalore offers an average package of around 8 Lakhs per year for its BTech programs, with top performers achieving significantly higher. Computer Science and Engineering (CSE) students generally secure high packages, often within the top salary brackets.\n\nKey Placement Highlights (2025/Recent):\n• Average Package: Approx. 8 Lakhs per year.\n• Highest Package: Reached up to 81.25 Lakhs per year in 2025.\n• Top Recruiters: Companies like Samsung, Silicon Labs, TCS, and Siemens visited the campus.\n• Placement Data: Over 3,000 companies participated in the 2025 campus recruitment, with over 2,500 offers made.",
                    null,
                    "JCER offers a dedicated Placement Cell that provides robust training and placement opportunities for all our students. We have consistently achieved excellent placement records with top multinational companies. Based on 2025 placement reports, the Jain Deemed-to-be University (Faculty of Engineering and Technology) in Bangalore offers an average package of around 8 Lakhs per year for its BTech programs, with top performers achieving significantly higher. Computer Science and Engineering (CSE) students generally secure high packages, often within the top salary brackets. Key Placement Highlights (2025/Recent): Average Package: Approx. 8 Lakhs per year. Highest Package: Reached up to 81.25 Lakhs per year in 2025. Top Recruiters: Companies like Samsung, Silicon Labs, TCS, and Siemens visited the campus. Placement Data: Over 3,000 companies participated in the 2025 campus recruitment, with over 2,500 offers made.");
            addTopic("13", "hi", "प्लेसमेंट",
                    "जेसीईआर एक समर्पित प्लेसमेंट सेल प्रदान करता है जो हमारे सभी छात्रों के लिए मजबूत प्रशिक्षण और प्लेसमेंट के अवसर प्रदान करता है। हमने शीर्ष बहुराष्ट्रीय कंपनियों के साथ लगातार उत्कृष्ट प्लेसमेंट रिकॉर्ड हासिल किए हैं। 2025 की प्लेसमेंट रिपोर्ट के आधार पर, बैंगलोर में जैन डीम्ड-टू-बी यूनिवर्सिटी (फैकल्टी ऑफ इंजीनियरिंग एंड टेक्नोलॉजी) अपने बीटेक कार्यक्रमों के लिए लगभग 8 लाख प्रति वर्ष का औसत पैकेज प्रदान करती है, जिसमें शीर्ष प्रदर्शन करने वाले छात्र काफी अधिक पैकेज प्राप्त करते हैं। कंप्यूटर विज्ञान और इंजीनियरिंग (सीएसई) के छात्र आमतौर पर उच्च पैकेज सुरक्षित करते हैं।\n\nप्रमुख प्लेसमेंट मुख्य अंश (2025/नवीनतम):\n• औसत पैकेज: लगभग 8 लाख प्रति वर्ष।\n• उच्चतम पैकेज: 2025 में 81.25 लाख प्रति वर्ष तक पहुंचा।\n• शीर्ष रिक्रूटर्स: सैमसंग, सिलिकॉन लैब्स, टीसीएस और सीमेंस जैसी कंपनियों ने कैंपस का दौरा किया।\n• प्लेसमेंट डेटा: 2025 कैंपस भर्ती में 3,000 से अधिक कंपनियों ने भाग लिया, जिसमें 2,500 से अधिक प्रस्ताव दिए गए।",
                    null);
            addTopic("13", "kn", "ಪ್ಲೇಸ್‌ಮೆಂಟ್ಸ್",
                    "ನಮ್ಮ ಎಲ್ಲಾ ವಿದ್ಯಾರ್ಥಿಗಳಿಗೆ ಉತ್ತಮ ತರಬೇತಿ ಮತ್ತು ಉದ್ಯೋಗಾವಕಾಶಗಳನ್ನು ಒದಗಿಸುವ ಮೀಸಲಾದ ಪ್ಲೇಸ್‌ಮೆಂಟ್ ಸೆಲ್ ಅನ್ನು ಜೆಸಿಇಆರ್ ಹೊಂದಿದೆ. ನಾವು ಉನ್ನತ ಬಹುರಾಷ್ಟ್ರೀಯ ಕಂಪನಿಗಳೊಂದಿಗೆ ಅತ್ಯುತ್ತಮ ಉದ್ಯೋಗ ದಾಖಲೆಗಳನ್ನು ಸಾಧಿಸಿದ್ದೇವೆ. 2025 ರ ಪ್ಲೇಸ್‌ಮೆಂಟ್ ವರದಿಗಳ ಆಧಾರದ ಮೇಲೆ, ಬೆಂಗಳೂರಿನ ಜೈನ್ ಡೀಮ್ಡ್-ಟು-ಬಿ ವಿಶ್ವವಿದ್ಯಾಲಯ (ಎಂಜಿನಿಯರಿಂಗ್ ಮತ್ತು ತಂತ್ರಜ್ಞಾನ ವಿಭಾಗ) ತನ್ನ ಬಿಟೆಕ್ ಕಾರ್ಯಕ್ರಮಗಳಿಗೆ ಸರಿಸುಮಾರು 8 ಲಕ್ಷ ಪ್ರತಿ ವರ್ಷ ಸರಾಸರಿ ಪ್ಯಾಕೇಜ್ ನೀಡುತ್ತದೆ, ಉನ್ನತ ಸಾಧಕರು ಗಣನೀಯವಾಗಿ ಹೆಚ್ಚಿನ ಪ್ಯಾಕೇಜ್ ಪಡೆಯುತ್ತಾರೆ. ಕಂಪ್ಯೂಟರ್ ಸೈನ್ಸ್ ಮತ್ತು ಎಂಜಿನಿಯರಿಂಗ್ (ಸಿಎಸ್‌ಇ) ವಿದ್ಯಾರ್ಥಿಗಳು ಸಾಮಾನ್ಯವಾಗಿ ಹೆಚ್ಚಿನ ಪ್ಯಾಕೇಜ್‌ಗಳನ್ನು ಪಡೆಯುತ್ತಾರೆ.\n\nಪ್ರಮುಖ ಪ್ಲೇಸ್‌ಮೆಂಟ್ ಮುಖ್ಯಾಂಶಗಳು (2025/ಇತ್ತೀಚಿನ):\n• ಸರಾಸರಿ ಪ್ಯಾಕೇಜ್: ಅಂದಾಜು 8 ಲಕ್ಷ ಪ್ರತಿ ವರ್ಷ.\n• ಅತಿ ಹೆಚ್ಚು ಪ್ಯಾಕೇಜ್: 2025 ರಲ್ಲಿ 81.25 ಲಕ್ಷ ಪ್ರತಿ ವರ್ಷ ತಲುಪಿದೆ.\n• ಉನ್ನತ ನೇಮಕಾತಿದಾರರು: ಸ್ಯಾಮ್ಸಂಗ್, ಸಿಲಿಕಾನ್ ಲ್ಯಾಬ್ಸ್, ಟಿಸಿಎಸ್, ಮತ್ತು ಸೀಮೆನ್ಸ್ ನಂತಹ ಕಂಪನಿಗಳು ಕ್ಯಾಂಪಸ್‌ಗೆ ಭೇಟಿ ನೀಡಿದ್ದವು.\n• ಪ್ಲೇಸ್‌ಮೆಂಟ್ ಡೇಟಾ: 2025 ರ ಕ್ಯಾಂಪಸ್ ನೇಮಕಾತಿಯಲ್ಲಿ 3,000 ಕ್ಕೂ ಹೆಚ್ಚು ಕಂಪನಿಗಳು ಭಾಗವಹಿಸಿದ್ದು, 2,500 ಕ್ಕೂ ಹೆಚ್ಚು ಆಫರ್‌ಗಳನ್ನು ನೀಡಲಾಗಿದೆ.",
                    null);

            // Topic #: Contact
            addTopic("#", "en", "Contact Us", "Contact us via email at principal@jcer.in or call us at 09448693987.",
                    "images/contact.jpg.jpeg");
            addTopic("#", "hi", "संपर्क करें",
                    "आप हमें principal@jcer.in पर ईमेल कर सकते हैं या 09448693987 पर कॉल कर सकते हैं।",
                    "images/contact.jpg.jpeg");
            addTopic("#", "kn", "ಸಂಪರ್ಕಿಸಿ",
                    "ನೀವು ನಮಗೆ principal@jcer.in ನಲ್ಲಿ ಇಮೇಲ್ ಮಾಡಬಹುದು ಅಥವಾ 09448693987 ಗೆ ಕರೆ ಮಾಡಬಹುದು.",
                    "images/contact.jpg.jpeg");
        }

                private void addTopic(String id, String lang, String title, String text, String imageUrl) {
                        addTopic(id, lang, title, text, imageUrl, null);
                }

                private void addTopic(String id, String lang, String title, String text, String imageUrl,
                                String spokenText) {
                        db.computeIfAbsent(id, k -> new HashMap<>()).put(lang,
                                        new TopicInfo(title, text, imageUrl, spokenText));
                }

                @Override
                public void handle(HttpExchange exchange) throws IOException {
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

                        String query = exchange.getRequestURI().getQuery();
                        String topic = "1";
                        String lang = "en";

                        if (query != null) {
                                String[] params = query.split("&");
                                for (String param : params) {
                                        String[] pair = param.split("=");
                                        if (pair.length > 1) {
                                                try {
                                                        if (pair[0].equals("topic"))
                                                                topic = URLDecoder.decode(pair[1],
                                                                                StandardCharsets.UTF_8.name());
                                                        if (pair[0].equals("lang"))
                                                                lang = URLDecoder.decode(pair[1],
                                                                                StandardCharsets.UTF_8.name());
                                                } catch (UnsupportedOperationException e) {
                                                }
                                        }
                                }
                        }

                        System.out.println("Request: topic=" + topic + ", lang=" + lang);

                        Map<String, TopicInfo> topicMap = db.get(topic);
                        if (topicMap != null) {
                                TopicInfo info = topicMap.getOrDefault(lang, topicMap.get("en"));
                                String imageUrlJson = info.imageUrl != null
                                                ? String.format(", \"imageUrl\": \"%s\"", info.imageUrl)
                                                : "";
                                String spokenTextJson = info.spokenText != null
                                                ? String.format(", \"spokenText\": \"%s\"", escapeJson(info.spokenText))
                                                : "";
                                String jsonResponse = String.format("{\"title\": \"%s\", \"text\": \"%s\"%s%s}",
                                                escapeJson(info.title),
                                                escapeJson(info.text), imageUrlJson, spokenTextJson);
                                byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                                exchange.sendResponseHeaders(200, bytes.length);
                                try (OutputStream os = exchange.getResponseBody()) {
                                        os.write(bytes);
                                }
                        } else {
                                String error = "{\"error\": \"Topic not found\"}";
                                exchange.sendResponseHeaders(404, error.length());
                                try (OutputStream os = exchange.getResponseBody()) {
                                        os.write(error.getBytes());
                                }
                        }
                }

                private String escapeJson(String input) {
                        return input.replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                }

                private static class TopicInfo {
                        String title;
                        String text;
                        String imageUrl;
                        String spokenText;

                        TopicInfo(String title, String text, String imageUrl, String spokenText) {
                                this.title = title;
                                this.text = text;
                                this.imageUrl = imageUrl;
                                this.spokenText = spokenText;
                        }
                }
        }
}
