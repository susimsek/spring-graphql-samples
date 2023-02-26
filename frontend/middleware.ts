import {NextRequest, NextResponse} from "next/server";

export function middleware(request: NextRequest){
    const token = request.cookies.get("authToken")?.value
    const url = request.nextUrl.clone()

    if (url.pathname == "/login" && token) {
        url.pathname = '/'
        return NextResponse.redirect(url)
    } else if (url.pathname != "/login" && token === undefined) {
        url.pathname = '/login'
        url.searchParams.set("redirectUrl", request.nextUrl.pathname)
        return NextResponse.redirect(url)
    }
    return NextResponse.next()
}

export const config = {
    matcher: [
        /*
         * Match all request paths except for the ones starting with:
         * - api (API routes)
         * - _next/static (static files)
         * - favicon.ico (favicon file)
         */
        '/((?!api|_next|signup|activate|favicon.ico).*)',
    ],
}