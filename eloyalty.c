/* eLoyalty application for SmartDeck */

#pragma attribute("aid", "f0 00 00 01")
#pragma attribute("dir", "61 10 4f 4 f0 00 00 01 50 8 65 6c 6f 79 61 6c 74 79")

//#include <multos.h>
#include <multoscomms.h>
#include <multoscrypto.h>

#include <multosarith.h>
#include <string.h>
#include <ISO7816.h>
#include <multosccr.h> // for ZFlag()#define ERR_OK          0x9000
#define ERR_WRONGCLASS  0x6402
#define ERR_BAD_INS     0x6404
#define ERR_UNDERFLOW   0x6406

#define INS_DES      	0x10
#define INS_TRIPLEDES   0x11
#define INS_DES_ASM    	0x20
#define INS_SHA1	    0x32
#define INS_SHA	    	0x31

#define INS_RNG	    	0x40

#define INS_Modular_Addition  	0x50
#define INS_Modular_MUL	    	0x51
#define INS_Modular_EXP		 	0x52
#define INS_Modular_Reduction	0x53
#define INS_Modular_Inversion	0x54

#define MYAPP_CLA       0x80

typedef unsigned char BYTE;

/* Data from APDU; this is placed at PB[0] */
#pragma melpublic
BYTE apduData[255];

#pragma melstatic
int lenHash;
int velikostDat;
int i;
BYTE cipherText[255];
BYTE randomField[255];

BYTE MULField[255];
int velikostModField;

BYTE MULOpp1[255];
int velikostOpp1;

BYTE MULOpp2[255];
int velikostOpp2;

BYTE MODRes[255];

BYTE key[] = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x01, 0x02, 0x03,
		0x04, 0x05, 0x06, 0x07, 0x08, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
		0x08 };

void main(void) {

	/* Check class in APDU. */
	if (CLA != MYAPP_CLA)
		ExitSW(ERR_WRONGCLASS);

	/* Decode instruction. */
	switch (INS) {
	case INS_DES:
		/* Ensure case 3 command. */
		if (!CheckCase(4))
			ExitSW(ERR_WRONGCLASS);
		i = 0;
		velikostDat = Lc;
		for (velikostDat = Lc; i < velikostDat; i = i + 8) {
			DESECBEncipher(apduData + i, cipherText + i, key);
		}
		memcpy(apduData, cipherText, velikostDat);
		ExitLa(velikostDat);
		break;

	case INS_TRIPLEDES: // rozpracovano
		/* Ensure case 3 command. */
		if (!CheckCase(4))
			ExitSW(ERR_WRONGCLASS);

		velikostDat = Lc;
		lenHash = sizeof(key);
		//lenHash=0x10;

		__push((BYTE*) &key);				// klíè
		__push((BYTE*) &cipherText);
		__push((BYTE*) &apduData);
		__code(PRIM, 0xc1); 			// šifruj DES ECB - kód viz dokumentace

		memcpy(apduData, cipherText, 8);
		__push((BYTE*) &key + 8);	// klíè
		__push((BYTE*) &cipherText);
		__push((BYTE*) &apduData);
		__code(PRIM, 0xc1);

		memcpy(apduData, cipherText, 8);

		__push((BYTE*) &key);				// klíè
		__push((BYTE*) &cipherText);
		__push((BYTE*) &apduData);
		__code(PRIM, 0xc1);

		memcpy(apduData, cipherText, 8);

		__push((BYTE*) &key);
		__push((BYTE*) lenHash);
		__push((BYTE*) &cipherText);
		__push((BYTE*) &apduData);
		__code(PRIM, 0xD9);

		__push((BYTE*) &key);
		__push((BYTE*) lenHash);
		__push((BYTE*) &cipherText + 8);
		__push((BYTE*) &apduData + 8);
		__code(PRIM, 0xD9);

		__push((BYTE*) &key);
		__push((BYTE*) lenHash);
		__push((BYTE*) &cipherText + 16);
		__push((BYTE*) &apduData + 16);
		__code(PRIM, 0xD9);

		memcpy(apduData, cipherText, lenHash);
		ExitLa(lenHash);
		break;

	case INS_SHA1:
		/* Ensure case 3 command. */
		if (!CheckCase(4))
			ExitSW(ERR_WRONGCLASS);

		velikostDat = Lc;

		__push((BYTE*) velikostDat);
		__push((BYTE*) &cipherText);
		__push((BYTE*) &apduData);
		__code(PRIM, 0xCA);

		memcpy(apduData, cipherText, velikostDat);
		ExitLa(velikostDat);
		break;

	case INS_SHA:
		/* Ensure case 3 command. */
		if (!CheckCase(4))
			ExitSW(ERR_WRONGCLASS);

		velikostDat = Lc;
		lenHash = P1;
		__push((BYTE*) velikostDat);
		__push((BYTE*) lenHash);
		__push((BYTE*) &cipherText);
		__push((BYTE*) &apduData);
		__code(PRIM, 0xCF);
		memcpy(apduData, cipherText, lenHash);
		ExitLa(lenHash);
		break;

	case INS_RNG:
		/* Ensure case 3 command. */
		if (!CheckCase(2))
			ExitSW(ERR_WRONGCLASS);

		i = 0;

		for (velikostDat = P1; i < velikostDat; i = i + 8) {

			__code(PRIM, 0xC4);
			// __push((BYTE*)&randomField);                                  
			__code(STORE, (BYTE*) &randomField, 8);

			memcpy(apduData + i, randomField, 8);
		}
		ExitLa(velikostDat);
		break;

	case INS_Modular_Addition:
		/* Ensure case 4 command. */
		if (!CheckCase(4))
			ExitSW(ERR_WRONGCLASS);

		if (P1 == 0x01) {
			velikostOpp1 = Lc;
			memcpy(MULOpp1, apduData, velikostOpp1);
			ExitLa(velikostOpp1);
		}

		else if (P1 == 0x02) {
			velikostOpp2 = Lc;
			memcpy(MULOpp2, apduData, velikostOpp2);
			ExitLa(velikostOpp2);

		} else if (P1 == 0x03) {
			velikostModField = Lc;
			memcpy(MULField, apduData, velikostModField);

			/*
			 __push((BYTE*)velikostOpp1); // len opp
			 __push((BYTE*)velikostModField); // len modulus
			 __push((BYTE*)&MULOpp1);
			 __push((BYTE*)&MULField);
			 __code(PRIM, 0xC3);
			 
			 
			 __push((BYTE*)velikostOpp2); // len opp
			 __push((BYTE*)velikostModField); // len modulus
			 __push((BYTE*)&MULOpp2);
			 __push((BYTE*)&MULField);
			 __code(PRIM, 0xC3);
			 
			 */
			ADDN(128, MODRes, MULOpp1, MULOpp2);

			__push((BYTE*) velikostOpp2); // len opp
			__push((BYTE*) velikostModField); // len modulus
			__push((BYTE*) &MODRes);
			__push((BYTE*) &MULField);
			__code(PRIM, 0xC3);  //reduction

			memcpy(apduData, MODRes, velikostModField);
			ExitLa(velikostModField);
		} else {
			ExitSW(ERR_WRONGCLASS);
		}

		break;

	case INS_Modular_MUL:
		/* Ensure case 3 command. */
		if (!CheckCase(4))
			ExitSW(ERR_WRONGCLASS);

		if (P1 == 0x01) {
			velikostOpp1 = Lc;
			/*   	for(i= 0; i< Lc; i++) {
			 MULOpp1[i] = apduData[i];
			 }
			 ExitLa(0x01);*/

			memcpy(MULOpp1, apduData, velikostOpp1);
			ExitLa(velikostOpp1);
		}

		else if (P1 == 0x02) {
			velikostOpp2 = Lc;
			memcpy(MULOpp2, apduData, velikostOpp2);
			ExitLa(velikostOpp2);

		} else if (P1 == 0x03) {

			velikostDat = Lc;
			memcpy(MULField, apduData, velikostModField);

			__push((BYTE*) velikostDat); // lenMod
			__push((BYTE*) &MULOpp1);
			__push((BYTE*) &MULOpp2);
			__push((BYTE*) &MULField);
			__code(PRIM, 0xC8);

			memcpy(apduData, MULOpp1, velikostDat);
			ExitLa(velikostDat);
		} else {
			ExitSW(ERR_WRONGCLASS);
		}

		break;

	case INS_Modular_EXP:
		/* Ensure case 3 command. */
		if (!CheckCase(4))
			ExitSW(ERR_WRONGCLASS);

		if (P1 == 0x01) {
			velikostOpp1 = Lc;
			/*   	for(i= 0; i< Lc; i++) {
			 MULOpp1[i] = apduData[i];
			 }
			 ExitLa(0x01);*/

			memcpy(MULOpp1, apduData, velikostOpp1);
			ExitLa(velikostOpp1);
		}

		else if (P1 == 0x02) {
			velikostOpp2 = Lc;
			memcpy(MULOpp2, apduData, velikostOpp2);
			ExitLa(velikostOpp2);

		} else if (P1 == 0x03) {
			velikostModField = Lc;
			memcpy(MULField, apduData, velikostModField);

			__push((BYTE*) velikostOpp2); // lenExponent
			__push((BYTE*) velikostModField); // lenModulus
			__push((BYTE*) &MULOpp2);
			__push((BYTE*) &MULField);
			__push((BYTE*) &MULOpp1);
			__push((BYTE*) &MODRes);
			__code(PRIM, 0xC8);

			memcpy(apduData, MODRes, velikostModField);
			ExitLa(velikostModField);
		} else {
			ExitSW(ERR_WRONGCLASS);
		}

		break;

	case INS_Modular_Reduction:
		/* Ensure case 3 command. */
		if (!CheckCase(4))
			ExitSW(ERR_WRONGCLASS);

		if (P1 == 0x01) {

			velikostOpp1 = Lc;
			for (i = 0; i < Lc; i++) {
				;
				MULOpp1[i] = apduData[i];
			}
			ExitLa(0x01);
		}

		/*    else if(P1==0x02){
		 for(i= 0; i< Lc; i++) {
		 ;
		 MULOpp2[i] = apduData[i];
		 }
		 ExitLa(0x01);
		 }*/
		else if (P1 == 0x02) {
			velikostModField = Lc;

			for (i = 0; i < Lc; i++) {

				MULField[i] = apduData[i];
			}

			__push((BYTE*) velikostOpp1); // len opp
			__push((BYTE*) velikostModField); // len modulus
			__push((BYTE*) &MULOpp1);
			__push((BYTE*) &MULField);
			__code(PRIM, 0xC3);

			memcpy(apduData, MULOpp1, 0x01);
			ExitLa(0x01);
		} else {
			ExitSW(ERR_WRONGCLASS);
		}

		break;

	case INS_Modular_Inversion:
		/* Ensure case 4 command. */
		if (!CheckCase(4))
			ExitSW(ERR_WRONGCLASS);

		if (P1 == 0x01) {
			velikostOpp1 = Lc;
			memcpy(MULOpp1, apduData, velikostOpp1);
			ExitLa(velikostOpp1);
		}

		else if (P1 == 0x02) {
			velikostModField = Lc;
			memcpy(MULField, apduData, velikostModField);

			__push((BYTE*) velikostModField); // len modulus
			__push((BYTE*) &MULField);  //modulus
			__push((BYTE*) velikostOpp1);  // len opp
			__push((BYTE*) &MULOpp1); // operand
			__push((BYTE*) &MODRes); // result
			__code(PRIM, 0xD0, 0x01);

			memcpy(apduData, MODRes, velikostModField);
			ExitLa(velikostModField);
		} else {
			ExitSW(ERR_WRONGCLASS);
		}

		break;

	case INS_DES_ASM:
		/* Ensure case 3 command. */
		if (!CheckCase(4))
			ExitSW(ERR_WRONGCLASS);

		__push((BYTE*) &key);				// klíè
		__push((BYTE*) &cipherText);
		__push((BYTE*) &apduData);
		__code(PRIM, 0xc1); 			// šifruj DES ECB - kód viz dokumentace

		memcpy(apduData, cipherText, 8);
		ExitLa(sizeof(cipherText));
		break;

	default:
		ExitSW(ERR_BAD_INS);
	}

}

