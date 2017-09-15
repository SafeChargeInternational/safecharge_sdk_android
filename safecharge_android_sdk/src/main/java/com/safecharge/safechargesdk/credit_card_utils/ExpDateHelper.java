package com.safecharge.safechargesdk.credit_card_utils;

public class ExpDateHelper {
    private final int BUFFER_SIZE = 5;
    private final int SEPARATOR_INDEX = 2;


    private StringBuffer m_internalBuffer;

    private String m_month = null;
    private String m_year = null;

    public ExpDateHelper() {
        this.m_internalBuffer = new StringBuffer(BUFFER_SIZE);
    }

    public boolean shouldEnd() {
        return (m_internalBuffer.length() == BUFFER_SIZE);
    }

    public String getBufferString() {
        return m_internalBuffer.toString();
    }

    public boolean updateBuffer(String newString,
                                int start,
                                int end,
                                int insertionCount) {
        if (insertionCount > 0) { // insert
            String lastCharacter = newString.substring(newString.length() - 1);
            return this.updateInsert(lastCharacter);
        } else { //backspace
            if (m_internalBuffer.length() > 0) {

                int lastCharacterindex = m_internalBuffer.length() - 1 ;
                m_internalBuffer.deleteCharAt(start);

                if( lastCharacterindex == SEPARATOR_INDEX ) {
                    m_internalBuffer.deleteCharAt(m_internalBuffer.length() - 1 );
                }

                if (m_internalBuffer.length() < SEPARATOR_INDEX) {
                    m_month = null;
                } else {
                    m_year = null;
                }
            }
        }

        return false;
    }

    private boolean updateInsert(String insert) {
        boolean insertMade = false;

        if (m_month == null) {
            insertMade = this.updateMonth(insert);
        } else if (m_year == null) {
            insertMade = this.updateYear(insert);
        }

        return insertMade;
    }

    private boolean updateMonth(String insert) {
        if ( m_internalBuffer.length() == 0 && ((insert.compareTo("0") == 0) || (insert.compareTo("1") == 0)) ) {
            m_internalBuffer.append(insert);

            if (m_year != null) {
                return true;
            } else {
                return false;
            }

        } else {
            if (m_internalBuffer.length() == 0) {
                m_internalBuffer.append("0");
                m_internalBuffer.append(insert);
                m_internalBuffer.append("/");
                m_month = m_internalBuffer.substring(0, SEPARATOR_INDEX);
                if (m_year != null) {
                    return true;
                } else {
                    return false;
                }
            } else {
                //don't update second zero and greater than 2
                if ( (insert.compareTo("0") == 0 && m_internalBuffer.substring(0,1).compareTo("0") == 0) ||
                        ( insert.compareTo("2") > 0 ) ) {
                    return false;
                } else {
                    m_internalBuffer.append(insert);
                    m_internalBuffer.append("/");
                    m_month = m_internalBuffer.substring(0, SEPARATOR_INDEX);
                    if (m_year != null) {
                        return true;
                    } else {
                        return false;
                    }

                }
            }
        }
    }

    private boolean updateYear(String insert) {
        if (m_internalBuffer.length() == BUFFER_SIZE)
            return false;

        if ( ( m_internalBuffer.length() == BUFFER_SIZE - 1 ) && ( insert.compareTo("0") == 0 ) )
            return false;

        m_internalBuffer.append(insert);
        if( m_internalBuffer.length() == BUFFER_SIZE ) {
            m_year = m_internalBuffer.substring( (SEPARATOR_INDEX + 1),m_internalBuffer.length() );
            return true;
        }
       return false;
    }
}
