package com.safecharge.safechargesdk.credit_card_utils;
import java.util.regex.*;

public class CreditCardValidator {
    public enum CardType {
        None(),
        MasterCard(),
        Visa(),
        DinersClubInternational(),
        Amex(),
        Jcb(),
        DiscoverCard(),
        Maestro(),
        Switch(),
        Solo(),
        Laser(),
        PayDotCom(),
        EntropayMasterCard(),
        EntropayVisa(),
        IsraeliCard();

        @org.jetbrains.annotations.Contract(pure = true)
        public int getCardCVVLength() {
            switch (this) {
                case Amex:
                    return 4;
                default:
                    return 3;
            }
        }
    }




    static String extractFirst6(String input, int maxOffset) {
        return input.substring(0,Math.min(maxOffset,input.length()));
    }

    static int extractRangeFromString(String input,int maxOffset) {
        String first6 = CreditCardValidator.extractFirst6(input,maxOffset);
        try {
            return Integer.parseInt(first6, 10);
        } catch(RuntimeException e){
            System.out.print("invalid format");
            return 0;
        }
    }

    static boolean matchesRegex(String regex,String text) {

        try {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(text);
            return m.matches();
        } catch ( PatternSyntaxException regexExp ) {
            System.out.print("invalid regex:" + regex);
            return false;
        }
    }


    static CardType checkIsPayDotcom(String cardNumber) {
        CardType company = CardType.None;
        if (CreditCardValidator.matchesRegex("^542703[0-9]*",cardNumber)) {
            company = CardType.PayDotCom;
        } else if (CreditCardValidator.matchesRegex("^533805[0-9]*", cardNumber)) {
            company = CardType.EntropayVisa;
        }
        return company;
    }

    static boolean checkAdditionalDiscoverRange(int range) {
        return 644000 <= range && range <= 659999;
    }

    static boolean checkSoloRange19(int range) {
        return 676705 == range || 676718 == range || 676750 <= range
                && range <= 676762 || 676770 == range || 676798 == range;
    }

    static boolean checkSwitchRange19(int range) {
        return 493600 <= range && range <= 493699 || 675905 == range;
    }

    static boolean checkMaestroRange19(int range) {
        return 493698 <= range && range <= 493699 || 633498 == range;
    }

    static boolean checkSoloRange18(int range) {
        return 633461 == range || 633473 == range || 633478 == range
                || 633494 == range || 633499 == range || 676703 == range
                || 676740 == range || 676774 == range || 676779 == range
                || 676782 == range || 676795 == range;
    }

    static boolean checkSwitchRange18(int range) {
        return 490302 <= range && range <= 490309 || 490335 <= range
                && range <= 490339 || 491174 <= range && range <= 491182
                || 675938 <= range && range <= 675940;
    }

    static boolean checkAdditionalMaestroRange(int range) {
        return 56 <= range && range <= 58 || 60 <= range && range <= 69;
    }

    static boolean checkSoloRange16(int range) {
        return 633490 <= range && range <= 633493 || 633495 <= range
                && range <= 633497 || 676700 <= range && range <= 676702
                || 676706 <= range && range <= 676717 || 676719 <= range
                && range <= 676739 || 676741 <= range && range <= 676749
                || 676763 <= range && range <= 676769 || 676771 <= range
                && range <= 676773 || 676775 <= range && range <= 676778
                || 676780 <= range && range <= 676781 || 676783 <= range
                && range <= 676794 || 676796 <= range && range <= 676797
                || 676799 == range || 676704 == range;
    }

    static boolean checkSwitchRange16(int range) {
        return 491101 <= range && range <= 491102 || 675900 <= range
                && range <= 675904 || 675906 <= range && range <= 675937
                || 675941 <= range && range <= 675999 || 633300 == range
                || 564182 == range;
    }

    static boolean checkMaestroRange16(int range) {
        return 633302 <= range && range <= 633360 || 633450 <= range
                && range <= 633460 || 633462 <= range && range <= 633472
                || 633474 <= range && range <= 633475 || 633479 <= range
                && range <= 633480 || 633482 <= range && range <= 633489
                || 633477 == range;
    }

    static boolean startsWith(String str, String prefix) { // ??
        return str.startsWith(prefix);
    }

    static boolean isMasterCard(int range) {
        return (range >= 222100 && range <= 272099) || ( range >= 510000 && range <= 559999);
    }

    static boolean checkLaserCard(int range) {
        return 630477 <= range && range <= 630481 ||    // Reserved for future use
                630483 <= range && range <= 630484 ||   // Reserved for future use
                630485 == range ||                      // First Active                18
                630487 == range ||                      // EBS                         19
                630490 == range ||                      // Bank of Ireland             19
                630491 <= range && range <= 630492 ||   // Reserved for future use
                630493 == range ||                      // Allied Irish Bank (AIB)     19
                630494 == range ||                      // Allied Irish Bank (AIB)     19
                630495 <= range && range <= 630496 ||   // National Irish Bank         18
                630497 == range ||                      // Reserved for future use
                630498 == range ||                      // Ulster Bank Ireland Limited 19
                630499 == range ||                      // Permanent TSB               16
                677117 == range ||                      // Ulster Bank Ireland Limited 16
                677120 == range ||                      // First Active 16
                670695 == range;                        // Allied Irish Bank (AIB)     19
    }

    public static class CheckCardReturnType {
        public CardType cardType;
        public boolean exactMatch;

        public CheckCardReturnType(CardType company,boolean exactMatch) {
            this.cardType = company;
            this.exactMatch = exactMatch;
        }
    }

    static public CheckCardReturnType checkCardType(String input) {
        String numberOnly = input.replaceAll("[^0-9]","");

        int creditCardLength = numberOnly.length();
        String cardNumber = numberOnly;

        int range = CreditCardValidator.extractRangeFromString(numberOnly,6);
        CardType company = CardType.None;
        boolean exactMatch = false;

        switch (creditCardLength) {
            case 8:
                company = CardType.IsraeliCard;
                exactMatch = true;
                break;
            case 9:
                if (CreditCardValidator.isMasterCard(range)) {
                    company = CardType.MasterCard;
                    exactMatch = true;
                    break;
                }
                company = CardType.IsraeliCard;
                exactMatch = true;
            break;
            case 13:
                if (CreditCardValidator.startsWith(cardNumber,"4")) {
                    company = CardType.Visa;
                    exactMatch = true;
                }
            break;
            case 14:
                int number = CreditCardValidator.extractRangeFromString(numberOnly,3);

                if (CreditCardValidator.startsWith(cardNumber,"36") || CreditCardValidator.startsWith(cardNumber,"38") || (number >= 300 && number <= 305)) {
                company = CardType.DinersClubInternational;
                exactMatch = true;
                break;
            }
            break;
            case 15:
                if (CreditCardValidator.startsWith(cardNumber,"37") || CreditCardValidator.startsWith(cardNumber, "34")) {
                company = CardType.Amex;
                exactMatch = true;
                break;
            } else if (CreditCardValidator.startsWith(cardNumber,"2131") || CreditCardValidator.startsWith(cardNumber, "1800")) {
                company = CardType.Jcb;
                exactMatch = true;
                break;
            }
            case 16:
                if (CreditCardValidator.startsWith(cardNumber, "542703")) {
                company = CardType.PayDotCom;
                exactMatch = true;
            } else if (CreditCardValidator.startsWith(cardNumber,"459061") ||
                        CreditCardValidator.startsWith(cardNumber, "410162") ||
                        CreditCardValidator.startsWith(cardNumber, "431380") ||
                        CreditCardValidator.startsWith(cardNumber, "406742")) {
                company = CardType.EntropayVisa;
                exactMatch = true;
            } else if (CreditCardValidator.startsWith(cardNumber, "533805")) {
                company = CardType.EntropayMasterCard;
                exactMatch = true;
            } else if (CreditCardValidator.startsWith(cardNumber, "6011")) {
                company = CardType.DiscoverCard;
                exactMatch = true;
            } else if (CreditCardValidator.startsWith(cardNumber, "3")) {
                company = CardType.Jcb;
                exactMatch = true;
            } else if (isMasterCard(range)) {
                company = CardType.MasterCard;
                exactMatch = true;
            } else if (checkMaestroRange16(range)) {
                company = CardType.Maestro;
                exactMatch = true;
            } else if (checkSwitchRange16(range)) {
                company = CardType.Switch;
                exactMatch = true;
            } else if (checkSoloRange16(range)) {
                company = CardType.Solo;
                exactMatch = true;
            } else if (startsWith(cardNumber,"4")) {
                company = CardType.Visa;
                exactMatch = true;
            }
            break;

            case 18:
                if (range == 490303) {
                    company = CardType.Maestro;
                    exactMatch = true;
                } else if (CreditCardValidator.checkSwitchRange18(range)) {
                company = CardType.Switch;
                exactMatch = true;
            } else if (checkSoloRange18(range)) {
                company = CardType.Solo;
                exactMatch = true;
            }
            break;

            case 19:
                if (checkMaestroRange19(range)) {
                company = CardType.Maestro;
                exactMatch = true;
            } else if (checkSwitchRange19(range)) {
                company = CardType.Switch;
                exactMatch = true;
            } else if (checkSoloRange19(range)) {
                company = CardType.Solo;
                exactMatch = true;
            } else if (startsWith(cardNumber, "4")) {
                company = CardType.Visa;
                exactMatch = true;
            }
            break;

            default:
                if (matchesRegex("^3[47][0-9]{0,13}$",cardNumber )) {
                    company = CardType.Amex;
                } else if (matchesRegex("^3(0[0-5]|[68][0-9])[0-9]{0,11}$",cardNumber )) {
                    company = CardType.DinersClubInternational;
                } else if (matchesRegex("^4[0-9]{0,15}$",cardNumber )) {
                    company = CardType.Visa;
                    if (matchesRegex("^459061[0-9]*",cardNumber) ||
                        matchesRegex("^410162[0-9]*",cardNumber) ||
                        matchesRegex("^431380[0-9]*",cardNumber) ||
                        matchesRegex("^406742[0-9]*",cardNumber)) {
                        company = CardType.EntropayVisa;
                }
            } else if ( creditCardLength < 6 ) {
                if (matchesRegex("^(5|2)[0-9]{0,15}$",cardNumber)) {
                    company = CardType.MasterCard;
                    CardType check = checkIsPayDotcom(cardNumber);
                    if (check != CardType.None) {
                        company = check;
                    }
                } else {
                    company = CardType.None;
                }
            } else if (isMasterCard(range)) {
                company = CardType.MasterCard;
                CardType check = checkIsPayDotcom(cardNumber);
                if (check != CardType.None) {
                    company = check;
                }
            } else {
                company = CardType.None;
            }
            break;
        }

        if (checkLaserCard(range)) {
            company = CardType.Laser;
        }

        // Maestro: we don't know the length so we check only according to
        // the initial numbers
        // Switch/Solo ranges take place precedence over Maestro ranges i.e
        // use Switch rules
        if (company == CardType.None) { // 'not determined yet
            int tmpRange = extractRangeFromString(numberOnly, 2);
            if (checkAdditionalDiscoverRange(range)) {
                company = CardType.DiscoverCard;
            } else if (startsWith(cardNumber, "50") || checkAdditionalMaestroRange(tmpRange)) {
                company = CardType.Maestro;
            } else {
                company = CardType.None;
            }
        }

        return new CheckCardReturnType(company,exactMatch);
    }

    public static String formatBy4(String input) {
        String numberOnly = input.replaceAll("[^0-9]","");
        String formatted4 = "";
        String formatted = "";

        for ( int i = 0 ; i <numberOnly.length(); i++ ) {
            if ( formatted4.length() == 4 ) {
                formatted+= formatted4 + " ";
                formatted4 = "";
            }
            formatted4+=numberOnly.charAt(i);
        }

        formatted += formatted4;

        return formatted;
    }


    public static boolean luhnCheck(String number){
        String numberOnly = number.replaceAll("[^0-9]","");
        if ( numberOnly.length() < 12 ) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;
        for (int i = numberOnly.length() - 1; i >= 0; i--)
        {
            int n = Integer.parseInt(numberOnly.substring(i, i + 1));
            if (alternate)
            {
                n *= 2;
                if (n > 9)
                {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}
