/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/
/**
 * This class file was automatically generated by jASN1 (http://www.openmuc.org)
 */

package org.openmuc.openiec61850.internal.mms.asn1;

import java.io.IOException;
import java.io.InputStream;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.BerIdentifier;
import org.openmuc.jasn1.ber.BerLength;
import org.openmuc.jasn1.ber.types.BerBoolean;

public final class ReadRequest {

	public final static BerIdentifier identifier = new BerIdentifier(BerIdentifier.UNIVERSAL_CLASS,
			BerIdentifier.CONSTRUCTED, 16);
	protected BerIdentifier id;

	public byte[] code = null;
	public BerBoolean specificationWithResult = null;

	public VariableAccessSpecification variableAccessSpecification = null;

	public ReadRequest() {
		id = identifier;
	}

	public ReadRequest(byte[] code) {
		id = identifier;
		this.code = code;
	}

	public ReadRequest(BerBoolean specificationWithResult, VariableAccessSpecification variableAccessSpecification) {
		id = identifier;
		this.specificationWithResult = specificationWithResult;
		this.variableAccessSpecification = variableAccessSpecification;
	}

	public int encode(BerByteArrayOutputStream berOStream, boolean explicit) throws IOException {

		int codeLength;

		if (code != null) {
			codeLength = code.length;
			for (int i = code.length - 1; i >= 0; i--) {
				berOStream.write(code[i]);
			}
		}
		else {
			codeLength = 0;
			int sublength;

			sublength = variableAccessSpecification.encode(berOStream, true);
			codeLength += sublength;
			codeLength += BerLength.encodeLength(berOStream, sublength);
			codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.CONSTRUCTED, 1))
					.encode(berOStream);

			if (specificationWithResult != null) {
				codeLength += specificationWithResult.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 0))
						.encode(berOStream);
			}

			codeLength += BerLength.encodeLength(berOStream, codeLength);
		}

		if (explicit) {
			codeLength += id.encode(berOStream);
		}

		return codeLength;

	}

	public int decode(InputStream iStream, boolean explicit) throws IOException {
		int codeLength = 0;
		int subCodeLength = 0;
		int choiceDecodeLength = 0;
		BerIdentifier berIdentifier = new BerIdentifier();
		boolean decodedIdentifier = false;

		if (explicit) {
			codeLength += id.decodeAndCheck(iStream);
		}

		BerLength length = new BerLength();
		codeLength += length.decode(iStream);

		if (subCodeLength < length.val) {
			if (decodedIdentifier == false) {
				subCodeLength += berIdentifier.decode(iStream);
				decodedIdentifier = true;
			}
			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 0)) {
				specificationWithResult = new BerBoolean();
				subCodeLength += specificationWithResult.decode(iStream, false);
				decodedIdentifier = false;
			}
		}
		if (subCodeLength < length.val) {
			if (decodedIdentifier == false) {
				subCodeLength += berIdentifier.decode(iStream);
				decodedIdentifier = true;
			}
			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.CONSTRUCTED, 1)) {
				subCodeLength += new BerLength().decode(iStream);
				variableAccessSpecification = new VariableAccessSpecification();
				choiceDecodeLength = variableAccessSpecification.decode(iStream, null);
				if (choiceDecodeLength != 0) {
					decodedIdentifier = false;
					subCodeLength += choiceDecodeLength;
				}
			}
			else {
				throw new IOException("Identifier does not macht required sequence element identifer.");
			}
		}
		if (subCodeLength != length.val) {
			throw new IOException("Decoded sequence has wrong length tag");

		}
		codeLength += subCodeLength;

		return codeLength;
	}

	public void encodeAndSave(int encodingSizeGuess) throws IOException {
		BerByteArrayOutputStream berOStream = new BerByteArrayOutputStream(encodingSizeGuess);
		encode(berOStream, false);
		code = berOStream.getArray();
	}
}
