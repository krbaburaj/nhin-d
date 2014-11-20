/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.common.crypto.impl;

import java.security.Key;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.common.crypto.PKCS11Credential;
import org.nhindirect.common.crypto.exceptions.CryptoException;

/**
 * Abstract base class for accessing key store pass phrases from a PKCS11 token.  Concrete implementations 
 * define methods for logging into the token.
 * @author Greg Meyer
 * @since 1.3
 */
public abstract class AbstractPKCS11TokenKeyStoreProtectionManager implements MutableKeyStoreProtectionManager
{

	protected PKCS11Credential credential;
	protected String keyStorePassPhraseAlias;
	protected String privateKeyPassPhraseAlias;
	protected KeyStore ks;
	
	/**
	 * Empty constructor.
	 * @throws CryptoException
	 */
	public AbstractPKCS11TokenKeyStoreProtectionManager() throws CryptoException
	{
		this.credential = null;
		this.keyStorePassPhraseAlias = "";
		this.privateKeyPassPhraseAlias = "";
	}
	
	/**
	 * Constructor that takes a credential interface for logging into the token.
	 * @param credential The credential used to log into the token.
	 * @param keyStorePassPhraseAlias The alias name of the key store key in the PKCS11 token.
	 * @param privateKeyPassPhraseAlias  The alias name of the private key protection key in the PKCS11 token.
	 * @throws CryptoException
	 */
	public AbstractPKCS11TokenKeyStoreProtectionManager(PKCS11Credential credential, String keyStorePassPhraseAlias, String privateKeyPassPhraseAlias) throws CryptoException
	{
		this.credential = credential;
		this.keyStorePassPhraseAlias = keyStorePassPhraseAlias;
		this.privateKeyPassPhraseAlias = privateKeyPassPhraseAlias;
		
		initTokenStore();
	}
	
	/**
	 * Sets the credential used to log into the token.
	 * @param credential The credential used to log into the token.
	 */
	public void setCredential(PKCS11Credential credential)
	{
		this.credential = credential;
	}
	
	/**
	 * Sets the alias name of the key store key in the PKCS11 token.
	 * @param keyStorePassPhraseAlias The alias name of the key store key in the PKCS11 token.
	 */
	public void setKeyStorePassPhraseAlias(String keyStorePassPhraseAlias)
	{
		this.keyStorePassPhraseAlias = keyStorePassPhraseAlias;
	}
	
	/**
	 * Sets the alias name of the private key protection key in the PKCS11 token.
	 * @param privateKeyPassPhraseAlias the alias name of the private key protection key in the PKCS11 token.
	 */
	public void setPrivateKeyPassPhraseAlias(String privateKeyPassPhraseAlias)
	{
		this.privateKeyPassPhraseAlias = privateKeyPassPhraseAlias;
	}
	
	/**
	 * Initializes access to the token.  This is implementation specific and may require user interaction.
	 * @throws CryptoException
	 */
	public abstract void initTokenStore() throws CryptoException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Key> getAllKeys() throws CryptoException
	{
		final Map<String, Key> keys = new HashMap<String, Key>();
		
		try 
		{
			final Enumeration<String> aliases = ks.aliases();
			while (aliases.hasMoreElements())
			{
				final String alias = aliases.nextElement();
				if (ks.isKeyEntry(alias))
				{
					try
					{
						final Key key = ks.getKey(alias, null);
						// make sure it's a secret key
						
						if (key instanceof SecretKey)
						{
							keys.put(alias, key);
						}
					}
					catch (Exception e)
					{
						// no-op, this might be a key that we don't care about
					}
				}
			}
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error extracting private key protection from PKCS11 token", e);
		}
		
		return keys;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key getPrivateKeyProtectionKey() throws CryptoException
	{
		try 
		{
			return ks.getKey(privateKeyPassPhraseAlias, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error extracting private key protection from PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key getKeyStoreProtectionKey() throws CryptoException
	{
		try 
		{
			return ks.getKey(keyStorePassPhraseAlias, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error extracting key store protection from PKCS11 token", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrivateKeyProtectionKey(Key key) throws CryptoException 
	{
		try 
		{
			ks.setKeyEntry(privateKeyPassPhraseAlias, key, null, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error storing private key protection into PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrivateKeyProtectionKeyAsBytes(byte[] key) throws CryptoException 
	{
		try 
		{
			final Key keySpec = new SecretKeySpec(key, "");
			ks.setKeyEntry(privateKeyPassPhraseAlias, keySpec, null, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error storing private key protection into PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrivateKeyProtectionKeyAsString(String key) throws CryptoException 
	{
		try 
		{
			final Key keySpec = new SecretKeySpec(key.getBytes(), "");
			ks.setKeyEntry(privateKeyPassPhraseAlias, keySpec, null, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error storing private key protection into PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearPrivateKeyProtectionKey() throws CryptoException
	{
		try 
		{
			ks.deleteEntry(privateKeyPassPhraseAlias);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error deleting private key protection from PKCS11 token", e);
		}	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setKeyStoreProtectionKey(Key key) throws CryptoException 
	{
		try 
		{
			ks.setKeyEntry(keyStorePassPhraseAlias, key, null, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error storing key store protection into PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setKeyStoreProtectionKeyAsBytes(byte[] key) throws CryptoException 
	{
		try 
		{
			final Key keySpec = new SecretKeySpec(key, "");
			ks.setKeyEntry(keyStorePassPhraseAlias, keySpec, null, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error storing key store protection into PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setKeyStoreProtectionKeyAsString(String key) throws CryptoException 
	{
		try 
		{
			final Key keySpec = new SecretKeySpec(key.getBytes(), "");
			ks.setKeyEntry(keyStorePassPhraseAlias, keySpec, null, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error storing key store protection into PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearKeyStoreProtectionKey() throws CryptoException 
	{
		try 
		{
			ks.deleteEntry(keyStorePassPhraseAlias);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error deleting key store protection from PKCS11 token", e);
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setKey(String alias, Key key) throws CryptoException
	{
		try 
		{
			ks.setKeyEntry(alias, key, null, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error deleting key store protection from PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearKey(String alias) throws CryptoException
	{
		try 
		{
			ks.deleteEntry(alias);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error deleting key store protection from PKCS11 token", e);
		}		
	}
}
