document.addEventListener('DOMContentLoaded', () => {
    const langBtns = document.querySelectorAll('.lang-btn');
    const dialBtns = document.querySelectorAll('.dial-btn');
    const speakerBtn = document.getElementById('speaker-btn');
    const topicTitle = document.getElementById('topic-title');
    const topicText = document.getElementById('topic-text');
    const audioWaves = document.getElementById('audio-waves');
    const subBtnsDisplay = document.getElementById('sub-buttons-display');
    const topicImageContainer = document.getElementById('topic-image-container');
    const topicImage = document.getElementById('topic-image');

    let currentLang = 'en';
    let currentTopicId = null;
    let synth = window.speechSynthesis;
    let isSpeaking = false;
    let imageInterval;
    let currentTypingSequence = 0;

    // UI Translations
    const uiTranslations = {
        'en': {
            'header-title': 'JCER <span>Assistant</span>',
            'speaker-stop': '<i class="fa-solid fa-volume-high"></i> Stop Speaking',
            'welcome-title': 'Welcome to JCER',
            'welcome-text': 'Select a topic from the dialpad below.',
            'topics': {
                '1': 'About College',
                '1_principal': 'Principal Sir',
                '2': 'Admission',
                '3': 'CSE',
                '4': 'ECE',
                '5': 'AI & ML',
                '6': 'Mechanical',
                '7': 'Civil',
                '8': 'App Science',
                '9': 'MBA',
                '12': 'Facilities',
                '10': 'Rules',
                '#': 'Contact Us',
                '11': 'Canteen',
                '13': 'Placements'
            },
            'sub-btns': {
                'principal': '<i class="fa-solid fa-user-tie"></i> Meet Principal Sir',
                'ugcet': 'UGCET (KCET)',
                'dcet': 'DCET (Diploma)',
                'mgmt': 'MANAGEMENT',
                'faculty': ' Faculty'
            }
        },
        'hi': {
            'header-title': 'जेसीईआर <span>असिस्टेंट</span>',
            'speaker-stop': '<i class="fa-solid fa-volume-high"></i> बोलना बंद करें',
            'welcome-title': 'जेसीईआर में आपका स्वागत है',
            'welcome-text': 'नीचे दिए गए डायलपैड से एक विषय चुनें।',
            'topics': {
                '1': 'कॉलेज के बारे में',
                '1_principal': 'प्राचार्य महोदय',
                '2': 'प्रवेश',
                '3': 'सीएसई',
                '4': 'ईसीई',
                '5': 'एआई और एमएल',
                '6': 'मैकेनिकल',
                '7': 'सिविल',
                '8': 'एप्लाइड साइंस',
                '9': 'एमबीए',
                '12': 'सुविधाएं',
                '10': 'नियम',
                '#': 'संपर्क करें',
                '11': 'कैंटीन',
                '13': 'प्लेसमेंट'
            },
            'sub-btns': {
                'principal': '<i class="fa-solid fa-user-tie"></i> प्राचार्य से मिलें',
                'ugcet': 'यूजीसीईटी (KCET)',
                'dcet': 'डीसीईटी (डिप्लोमा)',
                'mgmt': 'प्रबंधन',
                'faculty': ' संकाय'
            }
        },
        'kn': {
            'header-title': 'JCER <span>ಸಹಾಯಕ</span>',
            'speaker-stop': '<i class="fa-solid fa-volume-high"></i> ಮಾತನಾಡುವುದನ್ನು ನಿಲ್ಲಿಸಿ',
            'welcome-title': 'JCER ಗೆ ಸುಸ್ವಾಗತ',
            'welcome-text': 'ಕೆಳಗಿನ ಡಯಲ್‌ಪ್ಯಾಡ್‌ನಿಂದ ವಿಷಯವನ್ನು ಆಯ್ಕೆಮಾಡಿ.',
            'topics': {
                '1': 'ಕಾಲೇಜಿನ ಬಗ್ಗೆ',
                '1_principal': 'ಪ್ರಾಂಶುಪಾಲರು',
                '2': 'ಪ್ರವೇಶ',
                '3': 'ಸಿಎಸ್‌ಇ',
                '4': 'ಇಸಿಇ',
                '5': 'ಎಐ ಮತ್ತು ಎಂಎಲ್',
                '6': 'ಮೆಕ್ಯಾನಿಕಲ್',
                '7': 'ಸಿವಿಲ್',
                '8': 'ಅಪ್ಲೈಡ್ ಸೈನ್ಸ್',
                '9': 'ಎಂಬಿಎ',
                '12': 'ಸೌಲಭ್ಯಗಳು',
                '10': 'ನಿಯಮಗಳು',
                '#': 'ಸಂಪರ್ಕಿಸಿ',
                '11': 'ಕ್ಯಾಂಟೀನ್',
                '13': 'ಪ್ಲೇಸ್‌ಮೆಂಟ್ಸ್'
            },
            'sub-btns': {
                'principal': '<i class="fa-solid fa-user-tie"></i> ಪ್ರಾಂಶುಪಾಲರನ್ನು ಭೇಟಿ ಮಾಡಿ',
                'ugcet': 'ಯುಜಿಸಿಇಟಿ (KCET)',
                'dcet': 'ಡಿಸಿಇಟಿ (ಡಿಪ್ಲೊಮಾ)',
                'mgmt': 'ಮ್ಯಾನೇಜ್‌ಮೆಂಟ್',
                'faculty': ' ಬೋಧಕ ವರ್ಗ'
            }
        }
    };

    const langCodeMap = {
        'en': 'en-IN',
        'hi': 'hi-IN',
        'kn': 'kn-IN'
    };

    function updateUI() {
        const trans = uiTranslations[currentLang];
        
        // Update Header
        document.querySelector('.logo h1').innerHTML = trans['header-title'];
        
        // Update Footer Button
        speakerBtn.innerHTML = trans['speaker-stop'];
        
        // Update Dialpad Buttons
        dialBtns.forEach(btn => {
            const topicId = btn.getAttribute('data-topic');
            const topicNameSpan = btn.querySelector('.topic-name');
            if (topicNameSpan && trans.topics[topicId]) {
                topicNameSpan.textContent = trans.topics[topicId];
            }
        });

        // If no topic selected, update welcome message
        if (!currentTopicId) {
            topicTitle.textContent = trans['welcome-title'];
            topicText.textContent = trans['welcome-text'];
        } else {
            // Refresh current topic info in new language
            fetchTopicInfo(currentTopicId);
        }
    }

    // Handle Language Selection
    langBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            langBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentLang = btn.getAttribute('data-lang');
            console.log(`Language changed to ${currentLang}`);
            updateUI();
        });
    });

    // Handle Dial Button Clicks
    dialBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const topicId = btn.getAttribute('data-topic');
            btn.style.transform = 'scale(0.9)';
            setTimeout(() => btn.style.transform = '', 150);
            fetchTopicInfo(topicId);
        });
    });

    speakerBtn.addEventListener('click', stopSpeaking);

    // Fetch Information from Backend
    async function fetchTopicInfo(topicId) {
        currentTopicId = topicId;
        stopSpeaking();
        clearInterval(imageInterval);
        currentTypingSequence++;
        const seq = currentTypingSequence;
        
        try {
            topicTitle.textContent = "...";
            if (topicImageContainer) topicImageContainer.style.display = 'none';

            const response = await fetch(`/api/info?topic=${encodeURIComponent(topicId)}&lang=${encodeURIComponent(currentLang)}&t=${Date.now()}`);
            if (!response.ok) throw new Error("Failed to fetch data");
            const data = await response.json();

            // Handle Slideshow
            if (data.imageUrl && topicImage) {
                const images = data.imageUrl.split(',');
                if (images.length > 1) {
                    let currentIndex = 0;
                    topicImage.src = images[0];
                    topicImageContainer.style.display = 'block';
                    imageInterval = setInterval(() => {
                        currentIndex = (currentIndex + 1) % images.length;
                        topicImage.style.opacity = '0';
                        setTimeout(() => {
                            topicImage.src = images[currentIndex];
                            topicImage.style.opacity = '1';
                        }, 300);
                    }, 3000);
                } else {
                    topicImage.src = images[0];
                    topicImageContainer.style.display = 'block';
                }
                topicImageContainer.style.opacity = '0';
                setTimeout(() => topicImageContainer.style.opacity = '1', 100);
            }

            // Typing effect
            typeText(topicTitle, data.title, 0, true, seq);
            setTimeout(() => {
                if (currentTypingSequence === seq) {
                    typeText(topicText, data.text, 0, false, seq);
                }
            }, 500);

            // Handle Sub-Buttons
            subBtnsDisplay.innerHTML = '';
            const trans = uiTranslations[currentLang];

            if (topicId === '1') {
                const principalBtn = document.createElement('button');
                principalBtn.className = 'sub-btn special-btn';
                principalBtn.innerHTML = trans['sub-btns']['principal'];
                principalBtn.addEventListener('click', () => fetchTopicInfo('1_principal'));
                subBtnsDisplay.appendChild(principalBtn);
            }

            if (topicId.startsWith('2')) {
                [
                    { name: trans['sub-btns']['ugcet'], id: '2_ugcet' },
                    { name: trans['sub-btns']['dcet'], id: '2_dcet' },
                    { name: trans['sub-btns']['mgmt'], id: '2_management' }
                ].forEach(cat => {
                    if (topicId === cat.id) return;
                    const btn = document.createElement('button');
                    btn.className = 'sub-btn';
                    btn.textContent = cat.name;
                    btn.addEventListener('click', () => fetchTopicInfo(cat.id));
                    subBtnsDisplay.appendChild(btn);
                });
            }

            const facultyNames = {
                '3': trans.topics['3'],
                '4': trans.topics['4'],
                '5': trans.topics['5'],
                '6': trans.topics['6'],
                '7': trans.topics['7'],
                '8': trans.topics['8'],
                '9': trans.topics['9']
            };

            if (facultyNames[topicId]) {
                const facultyBtn = document.createElement('button');
                facultyBtn.className = 'sub-btn';
                facultyBtn.innerHTML = `<i class="fa-solid fa-users"></i> ${facultyNames[topicId]}${trans['sub-btns']['faculty']}`;
                facultyBtn.addEventListener('click', () => fetchTopicInfo(`${topicId}_faculty`));
                subBtnsDisplay.appendChild(facultyBtn);
            }

            speakText(data.spokenText || data.text, langCodeMap[currentLang] || 'en-US');

        } catch (error) {
            console.error('Error fetching topic:', error);
            topicTitle.textContent = "Error";
            topicText.textContent = "Connection lost.";
        }
    }

    function typeText(element, text, index, isTitle, seq, callback) {
        if (seq !== currentTypingSequence) return;
        
        if (index === 0) {
            element.textContent = "";
            // Split text by lines to handle list items differently
            const lines = text.split('\n');
            let currentLineIndex = 0;
            let currentCharIndex = 0;

            function processNext() {
                if (seq !== currentTypingSequence) return;
                if (currentLineIndex >= lines.length) {
                    if (callback) callback();
                    return;
                }

                const line = lines[currentLineIndex];
                const isListItem = line.trim().startsWith('•') || /^\d+\./.test(line.trim());

                if (isListItem && !isTitle) {
                    // Reveal list item one by one (line by line)
                    if (currentCharIndex === 0 && currentLineIndex > 0) {
                        element.textContent += '\n';
                    }
                    element.textContent += line;
                    currentLineIndex++;
                    currentCharIndex = 0;
                    setTimeout(processNext, 400); // 400ms delay between list items
                } else {
                    // Type character by character
                    if (currentCharIndex < line.length) {
                        if (currentCharIndex === 0 && currentLineIndex > 0) {
                            element.textContent += '\n';
                        }
                        element.textContent += line.charAt(currentCharIndex);
                        currentCharIndex++;
                        setTimeout(processNext, isTitle ? 15 : 5);
                    } else {
                        currentLineIndex++;
                        currentCharIndex = 0;
                        setTimeout(processNext, isTitle ? 15 : 5);
                    }
                }
            }
            processNext();
        }
    }

    function speakText(text, langCode) {
        stopSpeaking();
        if (text !== '') {
            let processedText = text;

            // Expand abbreviations for faculty topics to sound more professional
            if (currentTopicId && (currentTopicId.endsWith('_faculty') || currentTopicId === '1_principal')) {
                // English replacements
                processedText = processedText.replace(/\bDr\.?\b/g, 'Doctor');
                processedText = processedText.replace(/\bProf\.?\b/g, 'Professor');
                
                // Hindi replacements
                processedText = processedText.replace(/डॉ\.?/g, 'डॉक्टर');
                processedText = processedText.replace(/प्रो\.?/g, 'प्रोफेसर');
                
                // Kannada replacements
                processedText = processedText.replace(/ಡಾ\.?/g, 'ಡಾಕ್ಟರ್');
                processedText = processedText.replace(/ಪ್ರೊ\.?/g, 'ಪ್ರೊಫೆಸರ್');
                
                console.log("Speaking (expanded):", processedText);
            }

            const utterThis = new SpeechSynthesisUtterance(processedText);
            utterThis.lang = langCode;
            utterThis.rate = (langCode === 'hi-IN') ? 1.2 : 1.05;
            const voices = synth.getVoices();
            if (voices.length > 0 && langCode.startsWith('en')) {
                const maleVoice = voices.find(v => v.name.includes('David') || v.name.includes('Mark') || v.name.toLowerCase().includes('male'));
                if (maleVoice) utterThis.voice = maleVoice;
            }
            utterThis.onstart = () => { isSpeaking = true; audioWaves.classList.add('active'); };
            utterThis.onend = () => { isSpeaking = false; audioWaves.classList.remove('active'); };
            utterThis.onerror = () => { isSpeaking = false; audioWaves.classList.remove('active'); };
            synth.speak(utterThis);
        }
    }

    function stopSpeaking() {
        if (synth.speaking) {
            synth.cancel();
            isSpeaking = false;
            audioWaves.classList.remove('active');
        }
    }

    if (speechSynthesis.onvoiceschanged !== undefined) {
        speechSynthesis.onvoiceschanged = () => console.log("Voices loaded.");
    }

    // Initialize UI with default language
    updateUI();
});
