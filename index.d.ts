export namespace ReactNativeSSLRequest {
    interface Cookies {
        [cookieName: string]: string;
    }

    interface Header {
        [headerName: string]: string;
    }

    interface Options {
        body?: string | object,
        credentials?: string,
        headers?: Header;
        method?: 'DELETE' | 'GET' | 'POST' | 'PUT',
        timeoutInterval?: number,
    }

    interface Response {
        bodyString: string;
        headers: Header;
        status: number;
        url: string;
        json: () => Promise<{ [key: string]: any }>;
        text: () => Promise<string>;
    }
}

export declare function fetch(url: string, options: ReactNativeSSLRequest.Options): Promise<ReactNativeSSLRequest.Response>;
export declare function getCookies(domain: string): Promise<ReactNativeSSLRequest.Cookies>;
export declare function removeCookieByName(cookieName: string): Promise<void>;
